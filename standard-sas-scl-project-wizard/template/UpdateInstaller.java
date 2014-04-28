package ${package};

import java.util.ArrayList;
import java.util.List;

import com.sicpa.standard.tools.installer.BasicInstaller;
import com.sicpa.standard.tools.installer.task.InstallProcess;

public class UpdateInstaller extends BasicInstaller {

	@Override
	protected List<InstallProcess> getInstallTasks() {
		List<InstallProcess> tasks = new ArrayList<InstallProcess>();
		// add here install task

		return tasks;
	}

}
