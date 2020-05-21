package com.sicpa.standard.sasscl.devices.printer;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sicpa.standard.gui.utils.Pair;
import com.sicpa.standard.printer.controller.model.ModelDataMatrixEncoding;
import com.sicpa.standard.printer.controller.model.ModelDataMatrixFormat;
import com.sicpa.standard.printer.xcode.BitmapBlockFactory;
import com.sicpa.standard.printer.xcode.BlockFactory;
import com.sicpa.standard.printer.xcode.DatamatrixBlockFactory;
import com.sicpa.standard.printer.xcode.ExtendedCode;
import com.sicpa.standard.printer.xcode.ExtendedCodeFactory;
import com.sicpa.standard.printer.xcode.Option;
import com.sicpa.standard.sasscl.devices.printer.xcode.IExCodeBehavior;

public class TTTHExtendedCodeFactory implements IExCodeBehavior {

  private static final int BITMAP_CODE_HRC_HEIGHT = 9;

  private ModelDataMatrixFormat dmFormat;
  private ModelDataMatrixEncoding dmEncoding;

  private static final Logger logger = LoggerFactory.getLogger(TTTHExtendedCodeFactory.class);

  @Override
  public List<ExtendedCode> createExCodes(List<String> codes) {
    Validate.notNull(codes);
    List<ExtendedCode> res = new ArrayList<>();
    ExtendedCodeFactory ecf = new ExtendedCodeFactory();
    ecf.setBlockFactories(createBlockFactories());
    for (String code : codes) {
      res.add(ecf.create(createExCompositeCode(code)));
    }
    return res;
  }

  @Override
  public List<ExtendedCode> createExCodesPair(List<Pair<String, String>> codes) {
    return null;
  }

  private List<Object> createExCompositeCode(String code) {
    List<Object> compositeCode = new ArrayList<>();
    addLogoToCode(compositeCode, code);
    return compositeCode;
  }

  private void addLogoToCode(List<Object> compositeCode, String sicpaData) {
    compositeCode.add(sicpaData);
    compositeCode.add(TTTHDataExCodeUtil.getThBitmapLogo());
  }

  private List<BlockFactory> createBlockFactories() {
    List<BlockFactory> blockFactories = new ArrayList<>();
    blockFactories.add(createDatamatrixBlockFactory());
    blockFactories.add(createThLogoBitmapBlockFactory());
    return blockFactories;
  }

  private BitmapBlockFactory createThLogoBitmapBlockFactory() {
    BitmapBlockFactory thLogoBf = new BitmapBlockFactory();
    thLogoBf.addOption(Option.REVERSE);
    thLogoBf.setHeight(BITMAP_CODE_HRC_HEIGHT);
    return thLogoBf;
  }

  private DatamatrixBlockFactory createDatamatrixBlockFactory() {
    DatamatrixBlockFactory dmFactory = new DatamatrixBlockFactory();
    dmFactory.setModelDatamatrixEncoding(dmEncoding);
    dmFactory.setModelDatamatrixFormat(dmFormat);
    dmFactory.addOption(Option.INVERSE);
    dmFactory.addOption(Option.REVERSE);
    return dmFactory;
  }

  public void setDmFormat(ModelDataMatrixFormat dmFormat) {
    this.dmFormat = dmFormat;
  }

  public void setDmEncoding(ModelDataMatrixEncoding dmEncoding) {
    this.dmEncoding = dmEncoding;
  }

}
