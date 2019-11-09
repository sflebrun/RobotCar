/**
 * 
 */
package com.lebruns.steven.robotcar;

import java.util.HashMap;
import java.util.Map;

import com.lebruns.steven.robotcar.pipeline.Pipeline;


/**
 * @author Steven F. LeBrun
 *
 * The PipeWork is a container of Pipelines.  This is a singleton class
 * and is used to provide access to all the Pipelines to each controller
 * thread in the Robot Car Project.
 */
public class PipeWork
{
	static private PipeWork  pipework = null;
	static private String    valve    = "Thread Lock";
	
	private Map<PipeSink, Pipeline> pipes = null;
	
	/**
	 * Private Constructor.  Required for the Singleton Pattern.  This
	 * allows the class to control how many instances of this class can
	 * be created; in other words, it makes sure that only a single 
	 * instance is available.
	 */
	private PipeWork()
	{
		pipes = new HashMap<PipeSink, Pipeline>();
		
		init();
	}
	
	/**
	 * Provides access to the singleton PipeWork instance.
	 * 
	 * The first call to this method will result in the creation of the
	 * PipeWork instance.  If multiple calls come before the PipeWork 
	 * instance is created, then the first one to obtain the valve lock
	 * will create the instance.
	 * 
	 * @return Returns a reference to the PipeWork Instance.
	 */
	static PipeWork getInstance()
	{
		if ( pipework == null )
		{
			System.out.println(valve);
			
			// Obtain valve lock in order to prevent multiple calls to
			// this method from creating multiple PipeWork objects.
			try
			{
			synchronized (valve)
			{
				if ( pipework == null )
				{
					pipework = new PipeWork();
				}
			}  // end of lock
			}
			catch ( Exception exc )
			{
				System.out.println("Creating new PipeWork");
				System.out.println(exc.getMessage());
				exc.printStackTrace(System.out);
			}
		}
		
		return pipework;
		
	}   // end of getInstance()
	
	private void init()
	{
		Pipeline commands = new CommandPipe();
		Pipeline display  = new DisplayPipe();
		Pipeline watchdog = new WatchDogPipe();
		
		pipes.put(PipeSink.COMMAND_SINK, commands);
		pipes.put(PipeSink.DISPLAY_SINK, display);
		pipes.put(PipeSink.WATCHDOG_SINK, watchdog);
		

	}
	
	
	public Pipeline getPipeline( PipeSink type )
	{
		if ( pipes.containsKey(type) )
		{
			return pipes.get(type);
		}
		
		return null;
	}
	

}   // end of class PipeWork
