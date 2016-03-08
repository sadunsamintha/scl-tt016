import com.sicpa.standard.sasscl.common.log.OperatorLogger;
import com.sicpa.standard.sasscl.common.log.OperatorLoggerBehavior;

beans{
	OperatorLoggerBehavior log=new OperatorLoggerBehavior();
	OperatorLogger.setLoggerBehavior(log);
}