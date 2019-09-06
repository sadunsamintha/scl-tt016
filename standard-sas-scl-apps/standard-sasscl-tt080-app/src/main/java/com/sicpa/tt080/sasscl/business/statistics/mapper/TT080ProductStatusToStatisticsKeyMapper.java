package com.sicpa.tt080.sasscl.business.statistics.mapper;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

import com.sicpa.standard.sasscl.business.statistics.mapper.IProductStatusToStatisticKeyMapper;
import com.sicpa.standard.sasscl.model.ProductStatus;
import com.sicpa.standard.sasscl.model.statistics.StatisticsKey;

import static com.sicpa.standard.sasscl.model.statistics.StatisticsKey.GOOD;
import static com.sicpa.tt080.sasscl.model.TT080ProductStatus.DECLARED;


public class TT080ProductStatusToStatisticsKeyMapper implements IProductStatusToStatisticKeyMapper {

  private final IProductStatusToStatisticKeyMapper defaultMapper;
  protected final Multimap<ProductStatus, StatisticsKey> customMapping = ArrayListMultimap.create();

  public TT080ProductStatusToStatisticsKeyMapper(final IProductStatusToStatisticKeyMapper defaultMapper) {
    this.defaultMapper = defaultMapper;
    this.addProjectCustomKeys();
  }

  private void addProjectCustomKeys(){
    this.add(DECLARED, GOOD);
  }

  /**
   * Return the {@link StatisticsKey} associated to a {@link ProductStatus}.
   *
   * The resultant list will be a merge status from default and custom mappings
   * @param status ProductStatus we would like to find the list of statistics key associated
   * @return
   */
  @Override
  public Collection<StatisticsKey> getKey(final ProductStatus status) {
    Collection<StatisticsKey> keys = new ArrayList<>();
    final Collection<StatisticsKey> customKeys = convertToMutable(this.customMapping.get(status));

    Collection<StatisticsKey> defaultKeys;
    try{
      defaultKeys =  new ArrayList<>(this.defaultMapper.getKey(status));
    }catch (IllegalArgumentException e){
      defaultKeys = Collections.EMPTY_LIST;
    }

    final Collection<StatisticsKey> merge = merge(customKeys, defaultKeys);
    if(merge.isEmpty()) {
      throw new IllegalArgumentException("no statistics key for product status:"+status);
    }
    return Collections.unmodifiableCollection(merge);
  }

  @Override
  public void add(ProductStatus status, StatisticsKey key) {
    this.customMapping.put(status, key);
  }

  @Override
  public Collection<StatisticsKey> getAllKeys() {
    final ArrayList<StatisticsKey> statisticsKeys = new ArrayList<>(this.defaultMapper.getAllKeys());
    final Collection<StatisticsKey> customKeys = convertToMutable(this.customMapping.values());

    return merge(customKeys, statisticsKeys);
  }

  private static Collection<StatisticsKey> merge(final Collection<StatisticsKey> left, final Collection<StatisticsKey> right){
    final ArrayList<StatisticsKey> statisticsKeys = new ArrayList<>(left);
    statisticsKeys.addAll(right.stream()
        .filter(rightKey -> left.stream()
            .noneMatch(leftKey -> leftKey.equals(rightKey))).collect(Collectors.toList()));
    return statisticsKeys;
  }

  private static Collection<StatisticsKey> convertToMutable(final Collection<StatisticsKey> inmutableKeys){
    return inmutableKeys.stream().map(TT080ProductStatusToStatisticsKeyMapper::cloneStatsKey)
        .collect(Collectors.toList());
  }

  private static StatisticsKey cloneStatsKey(final StatisticsKey key) {
    return new StatisticsKey(key.getDescription());
  }
}
