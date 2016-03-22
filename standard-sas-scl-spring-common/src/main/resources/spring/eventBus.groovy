import com.sicpa.standard.sasscl.eventbus.EventBusWithUncaughtExceptionHandling
import com.sicpa.standard.client.common.eventbus.impl.DeadEventListener
import com.sicpa.standard.client.common.eventbus.service.EventBusService;

beans{
	def eventBus=new EventBusWithUncaughtExceptionHandling()
	def deadEventListener = new DeadEventListener()
	eventBus.register(deadEventListener)
	EventBusService.set(eventBus)
}

