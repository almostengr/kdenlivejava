package com.thealmostengineer.kdenlivetoyoutube;

import java.io.File;
import java.util.logging.Logger;

/**
 * 
 * @author almostengr, Kenny Robinson
 *
 */
public class ProcessPendingFile {
	static Logger logger = App.logger;
	
	public static void processPendingFile(File pendingFile) {
		try {
			if (pendingFile.getAbsolutePath().toLowerCase().endsWith(".gz") || 
					pendingFile.getAbsolutePath().toLowerCase().endsWith(".tar")) {
			
				logger.info("Processing " + pendingFile.getAbsolutePath());
				
				FileOperations.deleteFolder(App.appProperty.getProperty("renderDirectory")); // clean render directory
				ExtractProject.unpackageCompressTar(pendingFile.getAbsolutePath(), App.appProperty.getProperty("renderDirectory"));
				
				String kdenliveFileName = null;
				String videoOutputFileName = null;
				File[] renderDirFile = FileOperations.getFilesInFolder(App.appProperty.getProperty("renderDirectory")); // renderDir.listFiles();
				
				for (int i2 = 0; i2 < renderDirFile.length; i2++) {
					if (renderDirFile[i2].getAbsolutePath().endsWith("kdenlive")) {
						kdenliveFileName = renderDirFile[i2].getAbsolutePath();
						videoOutputFileName = App.appProperty.getProperty("outputDirectory") + 
								kdenliveFileName.substring(kdenliveFileName.lastIndexOf("/")) + ".mp4";
						videoOutputFileName = videoOutputFileName.replace(".kdenlive", "");
						logger.info("Kdenlive: " + kdenliveFileName);
						logger.info("Video Output: " + videoOutputFileName);
						break;
					} // end if
				} // end for
				
				FileOperations.createFolder(App.appProperty.getProperty("outputDirectory"));
				RenderFullLength.renderVideo(App.appProperty.getProperty("meltPath"), kdenliveFileName, videoOutputFileName); // run the kdenlive melt command
				
				if (App.appProperty.getProperty("timelapse").equalsIgnoreCase("yes")) {
					RenderTimelapse.renderTimelapse(videoOutputFileName);
				}
				ArchiveProject.archiveProject(pendingFile.getAbsolutePath(), App.appProperty.getProperty("archiveDirectory"));
			} // end if
		} catch (Exception e) {
			logger.info(e.getMessage());
			e.printStackTrace();
		} // end try
		
		logger.info("Done processing " + pendingFile.getAbsolutePath());
	}
}
