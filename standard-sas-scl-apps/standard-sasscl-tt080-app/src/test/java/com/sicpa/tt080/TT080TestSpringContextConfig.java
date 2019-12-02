package com.sicpa.tt080;

import java.util.Properties;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.Profile;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.Environment;

import com.sicpa.standard.client.common.provider.IProvider;
import com.sicpa.standard.client.common.security.SecurityModel;
import com.sicpa.standard.client.common.utils.ConfigUtils;
import com.sicpa.standard.sasscl.business.activation.IActivationBehavior;
import com.sicpa.standard.sasscl.business.activation.impl.activationBehavior.standard.StandardActivationBehavior;
import com.sicpa.standard.sasscl.config.xstream.CommonModelXStreamConfigurator;
import com.sicpa.standard.sasscl.controller.hardware.IHardwareController;
import com.sicpa.standard.sasscl.controller.productionconfig.IImplementationProvider;
import com.sicpa.standard.sasscl.controller.productionconfig.xstream.ProductionConfigXstreamConfigurator;
import com.sicpa.standard.sasscl.devices.bis.IBisAdaptor;
import com.sicpa.standard.sasscl.devices.plc.IPlcAdaptor;
import com.sicpa.standard.sasscl.provider.impl.ActivationBehaviorProvider;
import com.sicpa.standard.sasscl.provider.impl.AuthenticatorModeProvider;
import com.sicpa.standard.sasscl.provider.impl.BisProvider;
import com.sicpa.standard.sasscl.provider.impl.PlcProvider;
import com.sicpa.tt080.client.common.security.TT080SecurityModelWrapper;

@Configuration
@ImportResource({
    "classpath:/spring/security.xml",
    "classpath:/spring/productionConfig.xml",
    "classpath:/spring/custo/tt080/tt080-production-config.groovy"
})
public class TT080TestSpringContextConfig {

  @Autowired private SecurityModel securityModel;
  @Autowired private static Environment ENV;

  public TT080TestSpringContextConfig() {
    initXStream();
  }

  private void initXStream() {
    //Profile
    final CommonModelXStreamConfigurator commonModelXStreamConfigurator = new CommonModelXStreamConfigurator();
    commonModelXStreamConfigurator.configure(ConfigUtils.getXStream());

    //Production
    final ProductionConfigXstreamConfigurator productionConfigXstreamConfigurator = new ProductionConfigXstreamConfigurator();
    productionConfigXstreamConfigurator.configure(ConfigUtils.getXStream());
  }

  //Required helper BEANS for Test classes
  @Bean
  public TT080SecurityModelWrapper securityModelWrapperBean(){
    return new TT080SecurityModelWrapper<>(securityModel);
  }

  //Mocked BEANS
  @Bean(name = "hardwareController")
  public IHardwareController hardwareControllerBean(){
    return Mockito.mock(IHardwareController.class);
  }

  //BEANS required to initialize tested objects
  @Bean(name = "implementationProvider")
  public IImplementationProvider springImplementationProviderBean(){
    return Mockito.mock(IImplementationProvider.class);
  }

  @Bean(name = "plcProvider")
  public IProvider<IPlcAdaptor> plcAdaptorBean(){
    return new PlcProvider();
  }

  @Bean(name = "bisProvider")
  public IProvider<IBisAdaptor> bisProviderBean(){
    return new BisProvider();
  }

  @Bean(name = "authenticatorModeProvider")
  public IProvider<String> authenticatorModeProviderBean(){
    return new AuthenticatorModeProvider();
  }

  @Bean(name = "activationBehaviorProvider")
  public IProvider<IActivationBehavior> activationBehaviorProviderBean(){
    return new ActivationBehaviorProvider();
  }

  @Bean(name = "standardActivationBehavior")
  public IActivationBehavior standardActivationBehaviorBean(){
    return new StandardActivationBehavior();
  }

  @Profile("SAS")
  static class SASConfiguration{
    private static String MODE = "SAS";

    @Bean
    public static PropertySourcesPlaceholderConfigurer properties() throws Exception {
      return buildTestPropertiesByProfile(MODE);
    }
  }

  @Profile("SCL")
  static class SCLConfiguration{
    private static String MODE = "SCL";

    @Bean
    public static PropertySourcesPlaceholderConfigurer properties() throws Exception {
      return buildTestPropertiesByProfile(MODE);
    }
  }

  static PropertySourcesPlaceholderConfigurer buildTestPropertiesByProfile(final String mode){
    final PropertySourcesPlaceholderConfigurer pspc = new PropertySourcesPlaceholderConfigurer();
    Properties properties = new Properties();
    properties.setProperty("profile.path", "profiles/TT080-"+ mode);
    properties.setProperty("production.config.folder", "productionConfig-"+mode);
    pspc.setProperties(properties);
    return pspc;
  }
}
