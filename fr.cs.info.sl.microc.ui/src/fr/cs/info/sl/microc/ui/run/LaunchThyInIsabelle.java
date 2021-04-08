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


public class LaunchThyInIsabelle implements ILaunchShortcut {

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
					if (s instanceof IFile) {
						runInIsabelle(((IFile)s).getLocation().toString());
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
				runInIsabelle(((IFileEditorInput) input).getFile().getLocation().toString());
			}
		}

	}

	private void runInIsabelle(String fileName) {
		File arg = new File(fileName);
		System.out.println("isabelle jedit " + fileName);
		String cmd[] = {"isabelle", "jedit", arg.getName()};
		new ProcessHandler(cmd, arg.getParentFile()).start();
	}
}
