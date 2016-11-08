package com.sicpa.standard.sasscl.view.monitoring;

import com.google.common.eventbus.Subscribe;
import com.sicpa.standard.client.common.i18n.Messages;
import com.sicpa.standard.sasscl.view.LanguageSwitchEvent;
import org.apache.commons.lang.LocaleUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class ProductionStatisticsAggregatorFactory implements IProductionStatisticsAggregatorFactory {

    private static Logger logger = LoggerFactory.getLogger(ProductionStatisticsAggregatorFactory.class);

    protected String dateFormatForKeyDailyDetailed;
    protected String dateFormatForKeyDay;
    protected String dateFormatForKeyMonth;
    protected String dateFormatForKeyWeek;

    protected String language;

    @Override
    public IProductionStatisticsAggregator getInstance() {
        ProductionStatisticsAggregator productionStatisticsAggregator = createInstance();

        setupDateFormat();
        setInstanceProperties(productionStatisticsAggregator);

        return productionStatisticsAggregator;
    }

    @Subscribe
    public void handleLanguageSwitch(LanguageSwitchEvent evt) {
        language = evt.getLanguage();
    }

    protected ProductionStatisticsAggregator createInstance() {
        return new ProductionStatisticsAggregator();
    }

    private void setInstanceProperties(ProductionStatisticsAggregator productionStatisticsAggregator) {
        productionStatisticsAggregator.setDateFormatForKeyDailyDetailed(dateFormatForKeyDailyDetailed);
        productionStatisticsAggregator.setDateFormatForKeyDay(dateFormatForKeyDay);
        productionStatisticsAggregator.setDateFormatForKeyMonth(dateFormatForKeyMonth);
        productionStatisticsAggregator.setDateFormatForKeyWeek(dateFormatForKeyWeek);
    }

    private void setupDateFormat() {
        dateFormatForKeyDailyDetailed = initPattern("yyyy-MM-dd HH:mm:ss", "date.pattern.report.day.detail", language);
        dateFormatForKeyDay = initPattern("dd/MM/yy", "date.pattern.report.day.normal", language);
        dateFormatForKeyMonth = initPattern("dd/MM/yy", "date.pattern.report.day.month", language);
        dateFormatForKeyWeek = initPattern("dd/MM/yy", "date.pattern.report.day.week", language);
    }

    private String initPattern(String defaultPattern, String patternKey, String language) {
        String pattern = null;
        try {
            pattern = Messages.get(patternKey);
            // to test if the pattern is valid
            new SimpleDateFormat(pattern, getLocaleFromLanguage(language));
            return pattern;
        } catch (Exception e) {
            logger.error("invalid or pattern not found {}", pattern);
            return defaultPattern;
        }
    }

    private Locale getLocaleFromLanguage(String language) {
        return ((List<Locale>) LocaleUtils.availableLocaleList()).stream()
                .filter(l -> l.getLanguage().equals(language) && l.getCountry().equals("")).findFirst().get();
    }

    public void setLanguage(String language) {
        this.language = language;
    }
}