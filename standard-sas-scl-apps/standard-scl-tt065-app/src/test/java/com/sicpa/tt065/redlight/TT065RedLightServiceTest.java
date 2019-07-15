package com.sicpa.tt065.redlight;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.junit.Before;
import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class TT065RedLightServiceTest {

  private TT065RedLightService redLightService;
  private ITT065RemoteServer redLightMonitor;
  private CountDownLatch finished;

  @Before
  public void setUp() {
    this.redLightService = new TT065RedLightService();
    this.redLightMonitor = mock(ITT065RemoteServer.class);

    doAnswer(invocation -> true).when(this.redLightMonitor).isConnected();

    this.finished = new CountDownLatch(1);
    doAnswer(invocation -> {
      finished.countDown();
      return null;
    }).when(redLightMonitor).sendRedLightInfoToGlobalMonitoringTool(isA(RedLight.class));

    redLightService.setRemoteServer(redLightMonitor);
  }

  @Test
  public void startARedLight_shouldPutTheSystemInRedLightState(){
    this.redLightService.startARedLight("Forced Error for Test purpose");
    assertTrue("System should be in RedLight On state", this.redLightService.isInRedLightState());
  }

  @Test(expected = IllegalStateException.class)
  public void startARedLight_ThrowsException_whenTheSystemIsAlreadyInRedLightState(){
    this.redLightService.startARedLight("First Error");
    this.redLightService.startARedLight("Second Error");
  }

  @Test
  public void startARedLight_shouldNotifyGlobalMonitoring() throws InterruptedException {
    this.redLightService.startARedLight("Mock Error");
    boolean ended = finished.await(10, TimeUnit.SECONDS);
    assertThat(ended).isTrue();
    verify(redLightMonitor, times(1)).sendRedLightInfoToGlobalMonitoringTool(isA(RedLight.class));
  }

  @Test(expected = IllegalStateException.class)
  public void stopARedLight_ThrowsException_whenTheSystemIsNotInRedLightState(){
    this.redLightService.stopARedLight();
  }

  @Test
  public void stopRedLight_shouldPutTheSystemOutOfARedLightState(){
    this.redLightService.startARedLight("Forced Error for Test purpose");
    this.redLightService.stopARedLight();
    assertFalse("System should not be in RedLight state", this.redLightService.isInRedLightState());
  }

  @Test
  public void stopRedLight_shouldNotifyGlobalMonitoring() throws InterruptedException {
    this.redLightService.startARedLight("Mock Error");
    this.redLightService.stopARedLight();
    boolean ended = finished.await(10, TimeUnit.SECONDS);
    assertThat(ended).isTrue();
    verify(redLightMonitor, times(2)).sendRedLightInfoToGlobalMonitoringTool(isA(RedLight.class));
  }
}