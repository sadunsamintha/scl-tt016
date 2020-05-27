package com.sicpa.tt065.view.config.plc;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import java.time.Instant;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Buffer to hold,in memory, PLC request changes.
 * The buffer will hold all changes in entry order. Duplicated changes with same {@code logicalName} will be buffered as well.
 *
 * This class is thread-safe with all of its public methods covered.
 */
class TT065PLCMultiVarBuffer {

  private final Multimap<Integer, PLCVar> bufferedPLCUpdates = Multimaps.synchronizedMultimap(LinkedListMultimap.create());

  /**
   * Add changes to the buffer.
   *
   * No validations will be applied over the format of the {@code value} value or {@code lineIndex}.
   * However, logicalName should not be empty nor null.
   *
   * @param logicalName PLC variable logical name
   * @param value value to be sent to PLC.
   * @param lineIndex line identification.
   * @throws IllegalArgumentException if specified logicalName is {@code null} or empty
   * @throws NullPointerException if specified value is {@code null}
   */
  public void bufferPLCChange(final String logicalName, final String value, final int lineIndex){
    final String validName = Optional.ofNullable(logicalName).filter(s -> !s.isEmpty())
        .orElseThrow(() -> new IllegalArgumentException("logicalName cant be null or empty"));

    final String validValue = Optional.of(value).get();

    bufferedPLCUpdates.put(lineIndex, new PLCVar(validName, validValue));
  }

  /**
   * <b>Remove all</b> buffered changes from the buffer and return them as a {@link Map}, grouping the
   * changes per lineId. Each line will have a Map with all its updates. If changes for a same logicalName is found,
   * only the most recent one will be present at the returned map.
   *
   * There are <b>no guarantees on</b> the mutability, serializability, or thread-safety of the {@code Map} returned.
   *
   * @return
   */
  public Map<Integer, Map<String, String>> drainBuffer(){
    final HashMap<Integer, Map<String, String>> plcChangesPerLine = new HashMap<>();
    //per guava recommendation
    synchronized (bufferedPLCUpdates){
      final Map<Integer, Collection<PLCVar>> rawData = bufferedPLCUpdates.asMap();
      rawData.forEach((line, plcVars) -> plcChangesPerLine.put(line, collectLastModificationsPerVar(plcVars)));
      bufferedPLCUpdates.clear();
    }
    return plcChangesPerLine;
  }

  private static Map<String, String> collectLastModificationsPerVar(Collection<PLCVar> plcVars){
    final HashMap<String, String> modificationsByLogicalName = new HashMap<>();

    final Map<String, LinkedList<PLCVar>> collect = plcVars.stream().collect(Collectors.groupingBy(PLCVar::getName,
        Collectors.toCollection(LinkedList::new)));
    collect.forEach((logicalName, listOfChanges) -> {
      final PLCVar lastChange = listOfChanges.getLast();
      modificationsByLogicalName.put(logicalName, lastChange.getValue());
    });

    return modificationsByLogicalName;
  }

  /**
   * Remove all buffered changes, clearing the buffer state.
   */
  public void clearBuffer(){
    this.bufferedPLCUpdates.clear();
  }

  private static class PLCVar{

    private final String name;
    private final String value;
    private final Instant createdMoment;

    public PLCVar(String name, String value) {
      this.name = name;
      this.value = value;
      this.createdMoment = Instant.now();
    }

    public String getName() {
      return name;
    }

    public String getValue() {
      return value;
    }

    public long getInstant(){
      return createdMoment.getEpochSecond();
    }

    @Override
    public int hashCode() {
      return Objects.hash(getName());
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof PLCVar)) return false;
      PLCVar plcVar = (PLCVar) o;
      return Objects.equals(getName(), plcVar.getName()) &&
          Objects.equals(getValue(), plcVar.getValue());
    }

    @Override
    public String toString() {
      return "PLCVar{" +
          "name='" + name + '\'' +
          ", value='" + value + '\'' +
          '}';
    }
  }
}
