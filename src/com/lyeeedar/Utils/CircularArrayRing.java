/*******************************************************************************
 * Copyright (c) 2013 Philip Collin.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Philip Collin - initial API and implementation
 ******************************************************************************/
package com.lyeeedar.Utils;

import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;


/**
 * A Circular Array Ring Data Structure implementation written by me.
 * @author Lyeeedar
 *
 * @param <E>
 */
public class CircularArrayRing<E> extends AbstractCollection<E> implements Ring<E> {

	Object[] ring;
	int head;
	boolean filled = false;

	public CircularArrayRing(int i) 
	{
		ring = new Object[i];
		head = 0;
	}

	public CircularArrayRing()
	{
		ring = new Object[20];
		head = 0;
	}

	public boolean add(E e) {
		ring[head] = e;
		head++;
		if (head == ring.length)
		{
			head = 0;
			if (!filled)
			{
				filled = true;
			}
		}
		return false;

	}
	
	/**
	 * A method for updating values that is Non Destructive
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public E peek()
	{
		E e = (E) ring[head];
		head++;
		if (head == ring.length)
		{
			head = 0;
			if (!filled)
			{
				filled = true;
			}
		}
		return e;
	}


	@SuppressWarnings("unchecked")
	public E get(int index) throws IndexOutOfBoundsException {
		if (index >= ring.length)
		{
			throw new IndexOutOfBoundsException();
		}
		else if ((!filled) && (index > head))
		{
			throw new IndexOutOfBoundsException();
		}
		else if (index < 0)
		{
			throw new IndexOutOfBoundsException();
		}
		index = head-index-1;
		if (index < 0)
		{
			index = ring.length + index;
		}

		return (E) ring[index];
	}


	public Iterator<E> iterator() {
		return new Iterator<E>() {  

			int pos = head-1;
			int nextPos;
			public boolean hasNext() {
				nextPos = pos - 1;
				if (nextPos < 0)
				{
					nextPos = ring.length - 1 ;
				}
				
				if (nextPos == head)
				{
					return false;
				}
				else
				{
					return true;
				}
				

			}  

			@SuppressWarnings("unchecked")
			public E next() {  
				if(hasNext())
				{
					pos--;
					if (pos < 0)
					{
						pos = ring.length - 1;
					}
					return (E) ring[pos];
				}

				else
				{
					throw new NoSuchElementException();
				}

			}  

			public void remove() {  
				throw new UnsupportedOperationException(); 
			}  
		}; 
	}


	public int size() {

		if (!filled)
		{
			return head;
		}
		else
		{
			return ring.length;
		}
	}




}

interface Ring<E> extends Collection<E> {

    public E get(int index) throws IndexOutOfBoundsException;

    public Iterator<E> iterator();

    public int size();
}
