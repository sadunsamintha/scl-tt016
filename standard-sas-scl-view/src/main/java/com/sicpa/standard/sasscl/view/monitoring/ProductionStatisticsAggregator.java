package com.sicpa.standard.sasscl.view.monitoring;

import com.sicpa.standard.client.common.utils.DateUtils;
import com.sicpa.standard.sasscl.model.statistics.StatisticsKey;
import com.sicpa.standard.sasscl.monitoring.statistics.incremental.IncrementalStatistics;
import com.sicpa.standard.sasscl.view.report.ReportData;
import com.sicpa.standard.sasscl.view.report.ReportKey;
import com.sicpa.standard.sasscl.view.report.ReportPeriod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.Map.Entry;

public class ProductionStatisticsAggregator {

	private static final Logger logger = LoggerFactory.getLogger(ProductionStatisticsAggregator.class);

	protected Map<ReportKey, ReportData> mapData;
	protected ReportPeriod period;
	protected boolean groupByProduct;
	protected boolean dailyDetailed;

	public ProductionStatisticsAggregator() {
		this.mapData = new HashMap<>();
	}

	public void aggregate(final List<IncrementalStatistics> list, final ReportPeriod period,
						  final boolean groupByProduct, final boolean dailyDetail, Date minimumDate, Date maxDate) {
		this.period = period;
		this.groupByProduct = groupByProduct;
		this.dailyDetailed = dailyDetail;

		for (IncrementalStatistics aStats : list) {
			process(aStats, minimumDate, maxDate);
		}
	}

	protected void process(final IncrementalStatistics incrStats, Date minimumDate, Date maxDate) {
		try {
			ReportData data = createReportData();
			setReportData(data, incrStats);

			ReportKey key = createReportKey(incrStats, minimumDate, maxDate);

			addData(key, data);

		} catch (Exception e) {
			logger.error("", e);
		}
	}

	protected ReportKey createReportKey(IncrementalStatistics incrStats, Date minimumDate, Date maxDate) {
		ReportKey key = new ReportKey();

		key.setDate(getDateAsKey(incrStats.getStartTime(), incrStats.getStopTime(), minimumDate, maxDate));
		if (this.groupByProduct) {
			key.setProductionMode(incrStats.getProductionParameters().getProductionMode().getDescription());
			key.setSku(incrStats.getProductionParameters().getSku().getDescription());
		}

		return key;
	}

	protected ReportData createReportData() {
		return new ReportData();
	}

	protected void setReportData(ReportData reportData, IncrementalStatistics incrStats) {
		reportData.setRunningTime(
				(int) ((incrStats.getStopTime().getTime() - incrStats.getStartTime().getTime()) / 1000));

		Integer good = 0;
		Integer bad = 0;

		for (Entry<StatisticsKey, Integer> entry : incrStats.getProductsStatistics().getValues().entrySet()) {
			if (entry.getKey().toString().endsWith("good")) {
				good += entry.getValue();
			} else if (entry.getKey().toString().endsWith("bad")) {
				bad += entry.getValue();
			}
		}

		reportData.setBad(bad);
		reportData.setGood(good);
	}

	protected void addData(final ReportKey key, final ReportData data) {
		ReportData previous = this.mapData.get(key);
		if (previous == null) {
			previous = createReportData();
			this.mapData.put(key, previous);
		}
		previous.addData(data);
	}

	protected String dateFormatForKeyDailyDetailed;
	protected String dateFormatForKeyDay;
	protected String dateFormatForKeyMonth;
	protected String dateFormatForKeyWeek;

	protected String getDateAsKey(final Date startDate, final Date stopDate, Date minimumDate, Date maxDate) {
		String formatToUseStartDate = "";
		String formatToUseEndDate = "";

		Date dateStartToUse = startDate;
		Date dateEndToUse = stopDate;

		boolean useMinimumSelectedDate = true;
		boolean useMaximumSelectedDate = true;

		if (dailyDetailed) {
			formatToUseStartDate = dateFormatForKeyDailyDetailed;
			formatToUseEndDate = dateFormatForKeyDailyDetailed;
		} else {
			switch (this.period) {
				case DAY:
					formatToUseStartDate = dateFormatForKeyDay;
					formatToUseEndDate = "";
					break;
				case WEEK:
					formatToUseStartDate = dateFormatForKeyWeek;
					formatToUseEndDate = dateFormatForKeyWeek;

					int deltaStart = org.jdesktop.swingx.calendar.DateUtils.getDayOfWeek(startDate.getTime()) - 1;
					int deltaEnd = 7 - deltaStart;

					dateStartToUse = new Date(org.jdesktop.swingx.calendar.DateUtils.addDays(startDate.getTime(),
							-deltaStart));
					dateEndToUse = new Date(org.jdesktop.swingx.calendar.DateUtils.addDays(
							startDate.getTime(), deltaEnd));

					useMinimumSelectedDate = !isInSamePeriod(minimumDate, startDate, Calendar.WEEK_OF_YEAR);
					useMaximumSelectedDate = !isInSamePeriod(maxDate, stopDate, Calendar.WEEK_OF_YEAR);

					break;
				case MONTH:
					formatToUseStartDate = dateFormatForKeyMonth;
					formatToUseEndDate = dateFormatForKeyMonth;
					dateStartToUse = new Date(
							org.jdesktop.swingx.calendar.DateUtils.getStartOfMonth(startDate.getTime()));
					dateEndToUse = new Date(org.jdesktop.swingx.calendar.DateUtils.getEndOfMonth(stopDate.getTime()));
					useMinimumSelectedDate = !isInSamePeriod(minimumDate, startDate, Calendar.MONTH);
					useMaximumSelectedDate = !isInSamePeriod(maxDate, stopDate, Calendar.MONTH);
					break;
			}
		}

		if (!useMinimumSelectedDate) {
			// use the start of the period week/month if startDate and minimumDate are not the same week/month
			dateStartToUse = minimumDate;
		}

		if (!useMaximumSelectedDate) {
			// use the end of the period week/month if stopDate and minimumDate are not the same week/month
			dateEndToUse = maxDate;
			// when week wrong
			// +1 for month??
		}

		String res = DateUtils.format(formatToUseStartDate, dateStartToUse);
		if (!formatToUseEndDate.isEmpty()) {
			res += getDateSeparator() + DateUtils.format(formatToUseEndDate, dateEndToUse);
		}
		return res;
	}

	public boolean isInSamePeriod(Date d1, Date d2, int unit) {
		Calendar c = Calendar.getInstance();
		c.setTime(d1);
		int m1 = c.get(unit);

		c.setTime(d2);
		int m2 = c.get(unit);
		return m1 == m2;
	}

	protected String getDateSeparator() {
		return " - ";
	}

	public String getDateFormatForKeyDailyDetailed() {
		return dateFormatForKeyDailyDetailed;
	}

	public void setDateFormatForKeyDailyDetailed(String dateFormatForKeyDailyDetailed) {
		this.dateFormatForKeyDailyDetailed = dateFormatForKeyDailyDetailed;
	}

	public String getDateFormatForKeyDay() {
		return dateFormatForKeyDay;
	}

	public void setDateFormatForKeyDay(String dateFormatForKeyDay) {
		this.dateFormatForKeyDay = dateFormatForKeyDay;
	}

	public String getDateFormatForKeyMonth() {
		return dateFormatForKeyMonth;
	}

	public void setDateFormatForKeyMonth(String dateFormatForKeyMonth) {
		this.dateFormatForKeyMonth = dateFormatForKeyMonth;
	}

	public String getDateFormatForKeyWeek() {
		return dateFormatForKeyWeek;
	}

	public void setDateFormatForKeyWeek(String dateFormatForKeyWeek) {
		this.dateFormatForKeyWeek = dateFormatForKeyWeek;
	}

	public Map<ReportKey, ReportData> getMapData() {
		return this.mapData;
	}
}