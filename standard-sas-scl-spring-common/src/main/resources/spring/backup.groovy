import com.sicpa.standard.client.common.config.history.BackupService;

beans{

	def backupService= new BackupService()
	backupService.setProfilePath(profilePath)
	backupService.setBackupFolder('backup-config')
	backupService.setConfigFolder('config')
	backupService.setBackupMax(Integer.parseInt(props['backup.max']))
	backupService.doBackup();
}



