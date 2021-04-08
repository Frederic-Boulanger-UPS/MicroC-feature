package fr.cs.info.sl.microc.ui.run;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleConstants;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.IConsoleView;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;

public class ProcessHandler extends Thread {
	private String cmd[];
	private File wd;
	private static final String consoleName = "ProcessConsole";

	public ProcessHandler(String cmd[], File wd) {
		this.cmd = cmd;
		this.wd = wd;
	}

	private void reportError(String kind, String message) {
		System.err.println(message);
		Display disp = Display.getDefault();
		disp.syncExec(
				new Runnable() {
					public void run() {
						MessageDialog.openError(disp.getActiveShell(), kind, message);
					}
				}
				);
	}

	private void writeToConsole(MessageConsoleStream out, String kind, String message) {
		if (kind != null && kind.length() > 0) {
			out.println("# " + kind + ": " + message);
		} else {
			out.println(message);
		}
	}

	public String getPath() {
		StringBuilder res = new StringBuilder();
		
		Process p;
		try {
			p = Runtime.getRuntime().exec("sh -l");

			PrintStream ps = new PrintStream(p.getOutputStream());
			ps.println("echo $PATH");
			ps.flush();
			ps.close();
			BufferedReader cmdout = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line;

			while ((line = cmdout.readLine()) != null) {
				res.append(line);
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return System.getenv("PATH");
		}
		return res.toString();
	}

	public String[] getEnvWithShellPath() {
		Map<String, String> env = System.getenv();
		int arraysize = env.size();
		if (! env.containsKey("PATH")) {
			arraysize ++;
		}
		String nenv[] = new String[arraysize];
		int i = 0;
		for (Entry<String, String> e : env.entrySet()) {
			if (e.getKey().equals("PATH")) {
				continue;
			}
			nenv[i] = e.getKey() + "=" + e.getValue();
			System.err.println(nenv[i]);
			i++;
		}
		nenv[i] = "PATH=" + getPath();
		return nenv;
	}
	
	public void run() {
		Process p;
		// Create a console and make it visible
		MessageConsole cons = findConsole(consoleName);
		MessageConsoleStream console = cons.newMessageStream();

		IWorkbench wb = PlatformUI.getWorkbench();
		IWorkbenchWindow win = wb.getActiveWorkbenchWindow();
		IWorkbenchPage page = null;
		if (win != null) {
			page = win.getActivePage();
		}
		if (page != null) {
			IConsoleView view;
			try {
				view = (IConsoleView) page.showView(IConsoleConstants.ID_CONSOLE_VIEW);
				view.display(cons);
			} catch (PartInitException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		// end of console stuff
		
		try {
			// Enrich environment with PATH from login shell
			//p = Runtime.getRuntime().exec(cmd, getEnvWithShellPath(), wd);
			p = Runtime.getRuntime().exec(cmd, null, wd);

			BufferedReader cmdout = new BufferedReader(new InputStreamReader(p.getInputStream()));
			BufferedReader cmderr = new BufferedReader(new InputStreamReader(p.getErrorStream()));
			String line;

			try {
				while ((line = cmdout.readLine()) != null) {
					System.out.println(line);
					console.println(line);
				}
			} catch (IOException e) {
				reportError("Error", e.getMessage());
			}
			try {
				while ((line = cmderr.readLine()) != null) {
					System.err.println(line);
					writeToConsole(console, "Error", line);
				}
			} catch (IOException e) {
				reportError("Error", e.getMessage());
				writeToConsole(console, "Error", e.getMessage());
			}
			try {
				int status = p.waitFor();
				System.out.println("Process '" + this.cmd[0] + "' ended with status " + Integer.toString(status));
				writeToConsole(console, "Info", this.cmd[0] + "' ended with status " + Integer.toString(status));
			} catch (InterruptedException e) {
				reportError("Error", e.getMessage());
				writeToConsole(console, "Error", e.getMessage());
			}
		} catch (IOException e) {
			reportError("Error", e.getMessage());
			writeToConsole(console, "Error", e.getMessage());
			writeToConsole(console, "Info", "PATH is " + System.getenv("PATH"));
		}
		try {
			console.close();
		} catch (IOException e) {
			reportError("Error", e.getMessage());
			e.printStackTrace();
		}
	}

	private MessageConsole findConsole(String name) {
		ConsolePlugin plugin = ConsolePlugin.getDefault();
		IConsoleManager conMan = plugin.getConsoleManager();
		IConsole[] existing = conMan.getConsoles();
		for (int i = 0; i < existing.length; i++)
			if (name.equals(existing[i].getName()))
				return (MessageConsole) existing[i];
		//no console found, so create a new one
		MessageConsole myConsole = new MessageConsole(name, null);
		conMan.addConsoles(new IConsole[]{myConsole});
		return myConsole;
	}	
}
