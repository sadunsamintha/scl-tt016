package com.sicpa.tt065.redlight;

import com.google.common.base.Strings;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.StringJoiner;
import java.util.UUID;

import com.sicpa.standard.client.common.i18n.Messages;

/**
 * An Entity to represent a RedLight, with state transition.
 */
public class RedLight {
  private String uid;
  private RedLightState state;
  private Instant timeEntry;
  private Duration duration = null;
  private String description;

  private static final String DEFAULT_DESCRIPTION = "Unknown Reason";
  private static final int MAX_DESC_LENGTH = 430;

  enum RedLightState {
    ON("eventype.redlight.on"), OFF("eventype.redlight.off");

    private String code;

    RedLightState(String code) {
      this.code = code;
    }

    public String getCode() {
      return code;
    }
  }

  protected RedLight(){
  }

  /**
   * Start a RedLight
   * @param reasonToStart Reason to start a Red Light, usually an error or exception description
   * @return a new instance of a RedLight
   */
  protected static RedLight start(final String reasonToStart){
    final RedLight redLight = new RedLight();
    redLight.description = Strings.isNullOrEmpty(reasonToStart) ? DEFAULT_DESCRIPTION : redLight.truncate(reasonToStart);
    redLight.timeEntry = Instant.now();
    redLight.uid = UUID.randomUUID().toString().replaceAll("-", "");
    redLight.state = RedLightState.ON;
    return redLight;
  }

  /**
   * Deep copy of a RedLight
   * @param sourceRedLight Original RedLight instace to be copied into a new instace
   */
  protected static RedLight of(final RedLight sourceRedLight){
    final RedLight redLight = new RedLight();
    redLight.uid = sourceRedLight.uid;
    redLight.state = sourceRedLight.state;
    redLight.timeEntry = sourceRedLight.timeEntry;
    redLight.duration = sourceRedLight.duration;
    redLight.description = sourceRedLight.description;
    return redLight;
  }

  private String truncate(final String description){
    return description.substring(0, Math.min(MAX_DESC_LENGTH, description.length()));
  }

  /**
   * Turn Off a Red Light
   */
  protected void toggleOff(){
    if(this.isOn()){
      this.state = RedLightState.OFF;
      final Instant currentTimePoint = Instant.now();
      this.duration = Duration.between(this.timeEntry, currentTimePoint);
    }
  }

  /**
   * Get the actual Red Light state, if its ON or OFF
   * @return
   */
  public String getStateCode() {
    return state.getCode();
  }

  /**
   * Get the Red Light start date and time
   * @return
   */
  public Date getStartDate() {
   return Date.from(timeEntry);
  }

  public String getDescription() {
    return description;
  }

  /**
   * Get the total amount of time in which the red light stayed ON
   * @return total time in milliseconds
   */
  public long totalTime() {
    long totalTimeInRedLight = 0;
    if(!this.isOn()){
      totalTimeInRedLight = duration.toMillis();
    }
    return totalTimeInRedLight;
  }

  /**
   * Check if the red light is on ON state
   * @return TRUE if red light is ON, FALSE otherwise
   */
  public boolean isOn(){
    return this.state.equals(RedLightState.ON);
  }

  public String getUid() {
    return uid;
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", getClass().getSimpleName()+"[", "]")
        .add("UID="+getUid())
        .add("State="+ getStateCode())
        .add("Description="+getDescription())
        .toString();
  }

  /**
   * Convert a {@link RedLight} into a human readable and localized message
   * @param redLight A RedLight to be converted to string
   * @return complete description of the red light provided, localized, with its uid and status (ON or OFF)
   */
  public static String convertToReadableMessage(final RedLight redLight){
    final String redLightDescription = redLight.isOn() ? redLight.getDescription(): Long.toString(redLight.totalTime());
    return Messages.format(redLight.state.code, redLight.getUid(), redLightDescription);
  }
}
