package com.sicpa.tt065.devices.plc;

import com.google.common.eventbus.Subscribe;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.client.common.utils.StringMap;
import com.sicpa.standard.sasscl.devices.plc.IPlcValuesLoader;
import com.sicpa.standard.sasscl.devices.plc.PlcLineHelper;
import com.sicpa.standard.sasscl.devices.plc.variable.PlcVariableGroup;
import com.sicpa.standard.sasscl.devices.plc.variable.PlcVariableGroupEvent;
import com.sicpa.standard.sasscl.devices.plc.variable.descriptor.PlcVariableDescriptor;

/**
 * This class has the responsibility to read all plc vars, form lines and cabinet, and build all
 * editable fields from them.
 *
 * @Deprecated This class is temporary, and will be substituted by a core's solution on a near future
 */
public class TT065PLCGroupVarsBuilder {
  private List<PlcVariableGroup> linePlcVarGroup;
  private List<PlcVariableGroup> cabPlcVarGroups;
  private IPlcValuesLoader plcLoader;

  private static final Logger LOGGER = LoggerFactory.getLogger(TT065PLCGroupVarsBuilder.class);

  public void setLinePlcVarGroup(List<PlcVariableGroup> linePlcVarGroup) {
    this.linePlcVarGroup = linePlcVarGroup;
  }

  public void setCabPlcVarGroups(List<PlcVariableGroup> cabPlcVarGroups) {
    this.cabPlcVarGroups = cabPlcVarGroups;
  }

  public void setPlcLoader(IPlcValuesLoader plcLoader) {
    this.plcLoader = plcLoader;
  }

  public void loadFromPLC(){
    generateAllEditableVariableGroup(plcLoader.getValues());
  }

  private void generateAllEditableVariableGroup(Map<Integer, StringMap> values) {
    generateCabinetGroup(values.get(0));
    generateAllLinesEditableVariable(values);
  }

  private void generateAllLinesEditableVariable(Map<Integer, StringMap> values) {
    for (int i = 1; i < plcLoader.getLineCount() + 1; i++) {
      generateLineEditableVariables(i, linePlcVarGroup, values.get(i));
    }
  }

  private void generateCabinetGroup(final StringMap values) {
    //Clone Default PlcVars
    final List<PlcVariableGroup> cabPlcVarGroupsClone = new ArrayList<>();
    for (int i = 0; i < cabPlcVarGroups.size(); i++) {
      final PlcVariableGroup sourcePlcVarGroups = cabPlcVarGroups.get(i);
      final PlcVariableGroup clonePlcVarGroup = cloneAndUpdateWithValues(sourcePlcVarGroups, values);
      cabPlcVarGroupsClone.add(clonePlcVarGroup);
    }
    EventBusService.post(new PlcVariableGroupEvent(cabPlcVarGroupsClone, "cabinet"));
  }

  private PlcVariableGroup cloneAndUpdateWithValues(final PlcVariableGroup source, final StringMap withValues) {
    final PlcVariableGroup newGrp = new PlcVariableGroup();
    newGrp.setDescription(source.getDescription());

    for (PlcVariableDescriptor desc : source.getPlcVars()) {
      PlcVariableDescriptor newDesc = desc.clone();
      newDesc.setVarName(desc.getVarName());
      newDesc.initValue(withValues.get(desc.getVarName()));
      newGrp.addDescriptor(newDesc);
    }
    return newGrp;
  }

  private void generateLineEditableVariables(int index, List<PlcVariableGroup> lineVarGroups, StringMap values) {
    List<PlcVariableGroup> groups = PlcLineHelper.replaceLinePlaceholder(lineVarGroups, index);
    for (PlcVariableGroup grp : groups) {
      for (PlcVariableDescriptor desc : grp.getPlcVars()) {
        desc.initValue(values.get(desc.getVarName()));
      }
    }
    EventBusService.post(new PlcVariableGroupEvent(groups, "" + index));
  }

  /**
   * Observe Reload solicitations and, when received, get all actual plc values from its vars
   * and rebuild the editable plc field. As result a {@link PlcVariableGroupEvent} will be sent to the
   * bus service with the newly created editable fields
   *
   * @param evt
   */
  @Subscribe
  public void handleReloadEditableVariables(final ReloadPLCGroupVarsEvent evt){
    LOGGER.debug("PLC Reload requested by {}", evt.getRequester());
    this.loadFromPLC();
  }
}
