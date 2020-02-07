package spaceclipse.commands;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import spaceclipse.space.SpacEclipse;

public class Iniciar extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		new SpacEclipse().iniciar();
		return null;
	}

}
