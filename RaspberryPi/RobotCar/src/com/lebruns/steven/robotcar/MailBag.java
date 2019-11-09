/**
 * 
 */
package com.lebruns.steven.robotcar;

import java.util.HashMap;
import java.util.Map;
 
import com.lebruns.steven.robotcar.pipeline.MsgCapsule;

/**
 * @author Steven F. LeBrun
 *
 * The MailBag is a container of message capsules.  Message capsules that
 * have been processed and whose message was sent to the Arduino are placed 
 * in this container.  The message capsules are removed when a response to
 * the message has been received from the Arduino to finish processing.
 */
public class MailBag
{
	/**
	 * Lock is used to make instances of this class thread-safe.  We are
	 * not making the assumption that the container class used is already
	 * thread-safe.
	 */
	private Object lock = new Object();

	private Map<Integer, MsgCapsule> bag = null;
	/**
	 * 
	 */
	public MailBag()
	{
		bag = new HashMap<Integer, MsgCapsule>();
	}
	
	/**
	 * Adds a message capsule to the container.  The key used to store
	 * the message is the ID of message.  
	 * 
	 * @param message  The Message Capsule to be stored
	 * 
	 * @pre It is the caller's responsibility to make sure that only message
	 *      capsules that have been processed are placed in this container.
	 *      It is also the caller's responsibility to set the message capsule
	 *      state to 'has been processed'.
	 */
	public void insert(MsgCapsule message)
	{
		Integer key = new Integer(message.getMsgId());
		
		// Do not assume that container used is thread safe.
		synchronized (lock)
		{
			bag.put(key, message);
		}
	}

	/**
	 * Determines if a message capsule is in the container.
	 * 
	 * @param id The Unique ID of the message capsule being searched for.
	 * 
	 * @return Returns true if the Message Capsule is in the container.  
	 *         Returns false if the Message Capsule is not in the container.
	 */
	public boolean contains(int id)
	{
		Integer key = new Integer(id);
				
		return contains(key);
	} 
	
	private boolean contains(Integer key)
	{
		boolean  result = false;
		
		synchronized (lock)
		{
			result = bag.containsKey(key);
		}
		
		return result;
	} 
	
	/**
	 * Gets a Command Message from the container and removes it from 
	 * the container.
	 * 
	 * @param uid The Unique ID assigned to the Command Message.
	 * 
	 * @return Returns the Command Message if it is in the container.
	 *         Returns null if the Command Message is not in the container.
	 */
	public MsgCapsule fetch(int id)
	{
		Integer  key = new Integer(id);
		
		MsgCapsule message = null;
		
		if ( contains( id) )
		{
			synchronized(lock)
			{
				message = bag.remove(key);
			}
		}
		
		return message;
	}
	
	/**
	 * Gets a Command Message from the container.  The Command Message
	 * remains in the container.
	 * 
	 * @param uid The Unique ID assigned to the Command Message.
	 * 
	 * @return    Returns the Command Message if it is in the container.
	 *            Returns null if the Command Message is not in the container.
	 */
	public MsgCapsule get(int uid)
	{
		Integer key = new Integer(uid);
		
		MsgCapsule message = null;
		
		if ( contains(key) )
		{
			synchronized (lock)
			{
				message = bag.get(key);
			}
		}
		
		return message;
	}
	
	public int size()
	{
		return bag.size();
	}
	
}   // end of class MailBag

