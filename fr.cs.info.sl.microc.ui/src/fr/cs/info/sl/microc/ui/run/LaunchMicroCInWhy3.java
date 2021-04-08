package fr.cs.info.sl.microc.ui.run;

import java.io.File;

import org.eclipse.core.resources.IFile;
import org.eclipse.debug.ui.ILaunchShortcut;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;


public class LaunchMicroCInWhy3 implements ILaunchShortcut {

	@Override
	public void launch(ISelection selection, String mode) {
		if (selection.isEmpty()) {
			return;
		}
		// The selection should be a TreeSelection
		if (selection instanceof TreeSelection) {
			// Get the paths in the selection
			TreePath[] paths = ((TreeSelection) selection).getPaths();
			for (TreePath p : paths) {
				// Go through the segments of the paths
				for (int i = 0; i < p.getSegmentCount(); i++) {
					Object s = p.getSegment(i);
					// When a segment is a file, run it as a TESL simulation
//					if (s instanceof org.eclipse.core.internal.resources.File) {
//						runInWhy3(((org.eclipse.core.internal.resources.File)s).getLocation().toString());
//					}
					if (s instanceof IFile) {
						runInWhy3(((IFile)s).getLocation().toString());
					}
				}
			}
		}

	}

	@Override
	public void launch(IEditorPart editor, String mode) {
		if (editor != null) {
//			IWorkbenchPage page = editor.getSite().getPage();
			IEditorInput input = editor.getEditorInput();

			if (input instanceof IFileEditorInput) {
				runInWhy3(((IFileEditorInput) input).getFile().getLocation().toString());
			}
		}

	}

	// Launch why3 ide on a file
	private void runInWhy3(String fileName) {
		File arg = new File(fileName);
		System.out.println("why3 ide " + fileName);
		String cmd[] = {"why3", "ide", arg.getName()};
		new ProcessHandler(cmd, arg.getParentFile()).start();
	}

	// Launch why3 ide on a file, but configure why3 for detecting provers before
	// This is no longer necessary in the docker image because .why3.conf is kept and copied into the user home directory at startup
	/*
	private void runInWhy3_detect_provers(String fileName) {
		String cmd[] = {"why3", "config", "--detect-provers"};
		ProcessHandler ph = new ProcessHandler(cmd, null);
		ph.start();
		try {
			ph.join();
			File arg = new File(fileName);
			System.out.println("why3 ide " + fileName);
			String cmd2[] = {"why3", "ide", arg.getName()};
			new ProcessHandler(cmd2, arg.getParentFile()).start();
		} catch (InterruptedException e) {
			System.err.println(e.getMessage());
		}
	}
	*/
}
