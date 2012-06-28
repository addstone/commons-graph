package org.apache.commons.graph.collections;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import static java.lang.Math.floor;
import static java.lang.Math.log;
import static java.lang.Math.sqrt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.Set;

/**
 * A Fibonacci Heap implementation based on
 * <a href="http://staff.ustc.edu.cn/~csli/graduate/algorithms/book6/chap21.htm">University of Science and Technology of
 * China</a> lesson.
 *
 * <p><b>Note 1</b>: this class is NOT thread safe!</p>
 *
 * <p><b>Note 2</b>: this class doesn't support {@code null} values</p>
 *
 * @param <E> the type of elements held in this collection.
 */
public final class FibonacciHeap<E>
    implements Queue<E>
{

    /**
     * The <i>Phi</i> constant value.
     */
    private static final double LOG_PHI = log( ( 1 + sqrt( 5 ) ) / 2 );

    /**
     * A simple index of stored elements.
     */
    private final Set<E> elementsIndex = new HashSet<E>();

    /**
     * The comparator, or null if priority queue uses elements'
     * natural ordering.
     */
    private final Comparator<? super E> comparator;

    /**
     * The number of nodes currently in {@code H} is kept in {@code n[H]}.
     */
    private int size = 0;

    /**
     * {@code t(H)} the number of trees in the root list.
     */
    private int trees = 0;

    /**
     * {@code m(H)} the number of marked nodes in {@code H}.
     */
    private int markedNodes = 0;

    /**
     * The root of the tree containing a minimum key {@code min[H]}.
     */
    private FibonacciHeapNode<E> minimumNode;

    /**
     * Creates a {@link FibonacciHeap} that orders its elements according to their natural ordering.
     */
    public FibonacciHeap()
    {
        this( null );
    }

    /**
     * Creates a {@link FibonacciHeap} that orders its elements according to the specified comparator.
     *
     * @param comparator the comparator that will be used to order this queue.
     *                   If null, the natural ordering of the elements will be used.
     */
    public FibonacciHeap( /* @Nullable */Comparator<? super E> comparator )
    {
        this.comparator = comparator;
    }

    /**
     * {@inheritDoc}
     *
     * <pre>FIB-HEAP-INSERT(H, x)
     * 1  degree[x] &larr; 0
     * 2  p[x] &larr; NIL
     * 3  child[x] &larr; NIL
     * 4  left[x] &larr; x
     * 5  right[x] &larr; x
     * 6  mark[x] &larr; FALSE
     * 7  concatenate the root list containing x with root list H
     * 8  if min[H] = NIL or key[x] &lt; key[min[H]]
     * 9     then min[H] &larr; x
     * 10  n[H] &larr; n[H] + 1</pre>
     */
    public boolean add( E e )
    {
        if ( e == null )
        {
            throw new NullPointerException();
        }

        // 1-6 performed in the node initialization
        FibonacciHeapNode<E> node = new FibonacciHeapNode<E>( e );
        // 8'  if min[H] = NIL
        if ( isEmpty() )
        {
            // then min[H] <- x
            minimumNode = node;
        }
        else
        {
            // 7 concatenate the root list containing x with root list H
            minimumNode.getLeft().setRight( node );
            node.setLeft( minimumNode.getLeft() );
            node.setRight( minimumNode );
            minimumNode.setLeft( node );

            // 8''  if key[x] < key[min[H]]
            if ( compare( node, minimumNode ) < 0 )
            {
                // 9     then min[H] <- x
                minimumNode = node;
            }
        }

        // 10  n[H] <- n[H] + 1
        size++;

        elementsIndex.add( e );

        return true;
    }

    /**
     * {@inheritDoc}
     */
    public boolean addAll( Collection<? extends E> c )
    {
        for ( E element : c )
        {
            add( element );
        }

        return true;
    }

    /**
     * {@inheritDoc}
     */
    public void clear()
    {
        minimumNode = null;
        size = 0;
        trees = 0;
        markedNodes = 0;
        elementsIndex.clear();
    }

    /**
     * {@inheritDoc}
     */
    public boolean contains( Object o )
    {
        if ( o == null )
        {
            return false;
        }

        return elementsIndex.contains( o );
    }

    /**
     * {@inheritDoc}
     */
    public boolean containsAll( Collection<?> c )
    {
        if ( c == null )
        {
            return false;
        }

        for ( Object o : c )
        {
            if ( !contains( o ) )
            {
                return false;
            }
        }

        return true;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isEmpty()
    {
        return minimumNode == null;
    }

    /**
     * {@inheritDoc}
     */
    public Iterator<E> iterator()
    {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public boolean remove( Object o )
    {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public boolean removeAll( Collection<?> c )
    {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public boolean retainAll( Collection<?> c )
    {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public int size()
    {
        return size;
    }

    /**
     * {@inheritDoc}
     */
    public Object[] toArray()
    {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public <T> T[] toArray( T[] a )
    {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public E element()
    {
        if ( isEmpty() )
        {
            throw new NoSuchElementException();
        }
        return peek();
    }

    /**
     * {@inheritDoc}
     */
    public boolean offer( E e )
    {
        return add( e );
    }

    /**
     * {@inheritDoc}
     */
    public E peek()
    {
        if ( isEmpty() )
        {
            return null;
        }

        return minimumNode.getElement();
    }

    /**
     * {@inheritDoc}
     *
     * <pre>FIB-HEAP-EXTRACT-MIN(H)
     * 1  z &larr; min[H]
     * 2  if z &ne; NIL
     * 3      then for each child x of z
     * 4               do add x to the root list of H
     * 5                  p[x] &larr; NIL
     * 6           remove z from the root list of H
     * 7           if z = right[z]
     * 8              then min[H] &larr; NIL
     * 9              else min[H] &larr; right[z]
     * 10                   CONSOLIDATE(H)
     * 11           n[H] &larr; n[H] - 1
     * 12  return z</pre>
     */
    public E poll()
    {
        // 2  if z &ne; NIL
        if ( isEmpty() )
        {
            return null;
        }

        // 1  z <- min[H]
        FibonacciHeapNode<E> z = minimumNode;

        // 3  for each child x of z
        if ( z.getDegree() > 0 )
        {
            FibonacciHeapNode<E> x = z.getChild();
            FibonacciHeapNode<E> tempRight;

            do
            {
                tempRight = x.getRight();

                // 4  do add x to the root list of H
                x.getLeft().setRight( x.getRight() );
                x.getRight().setLeft( x.getLeft() );

                // 4  add x to the root list of H
                z.getLeft().setRight( x );
                x.setLeft( z.getLeft() );
                z.setLeft( x );
                x.setRight( z );

                // 5  p[x] <- NIL
                x.setParent( null );

                x = tempRight;
            }
            while ( x != z.getChild() );
        }

        // 6  remove z from the root list of H
        z.getLeft().setRight( z.getRight() );
        z.getRight().setLeft( z.getLeft() );

        // 7  if z = right[z]
        if ( z == z.getRight() )
        {
            // 8  min[H] <- NIL
            minimumNode = null;
        }
        else
        {
            // 9  min[H] <- right[z]
            minimumNode = z.getRight();
            // 10  CONSOLIDATE(H)
            consolidate();
        }

        // n[H] <- n[H] - 1
        size--;

        E minimum = z.getElement();
        elementsIndex.remove( minimum );
        return minimum;
    }

    /**
     * {@inheritDoc}
     */
    public E remove()
    {
        // FIB-HEAP-EXTRACT-MIN(H)

        if ( isEmpty() )
        {
            throw new NoSuchElementException();
        }

        return poll();
    }

    /**
     * Implements the {@code CONSOLIDATE(H)} function.
     *
     * <pre>CONSOLIDATE(H)
     * 1 for i &larr; 0 to D(n[H])
     * 2      do A[i] &larr; NIL
     * 3 for each node w in the root list of H
     * 4      do x &larr; w
     * 5         d &larr; degree[x]
     * 6         while A[d] &ne; NIL
     * 7            do y &larr; A[d]
     * 8               if key[x] &gt; key[y]
     * 9                  then exchange x &harr; y
     * 10                FIB-HEAP-LINK(H,y,x)
     * 11                A[d] &larr; NIL
     * 12                d &larr; d + 1
     * 13         A[d] &larr; x
     * 14 min[H] &larr; NIL
     * 15 for i &larr; 0 to D(n[H])
     * 16      do if A[i] &ne; NIL
     * 17            then add A[i] to the root list of H
     * 18                 if min[H] = NIL or key[A[i]] &le; key[min[H]]
     * 19                    then min[H] &larr; A[i]</pre>
     */
    private void consolidate()
    {
        if ( isEmpty() )
        {
            return;
        }

        // D( n[H] ) <= log_phi( n[H] )
        // -> log_phi( n[H] ) = log( n[H] ) / log( phi )
        // -> D( n[H] ) = log( n[H] ) / log( phi )
        int arraySize = ( (int) floor( log( size ) / LOG_PHI ) );

        // 1  for i <- 0 to D(n[H])
        List<FibonacciHeapNode<E>> nodeSequence = new ArrayList<FibonacciHeapNode<E>>( arraySize );
        for ( int i = 0; i < arraySize; i++ )
        {
            // 2      do A[i] <- NIL
            nodeSequence.add( i, null );
        }

        // 3  for each node x in the root list of H
        // 4  do x &larr; w
        FibonacciHeapNode<E> x = minimumNode;
        do
        {
            // 5  d <- degree[x]
            int degree = x.getDegree();

            // 6  while A[d] != NIL
            while ( nodeSequence.get( degree ) != null )
            {
                // 7  do y <- A[d]
                FibonacciHeapNode<E> y = nodeSequence.get( degree );

                // 8  if key[x] > key[y]
                if ( compare( x, y ) > 0 )
                {
                    // 9  exchange x <-> y
                    FibonacciHeapNode<E> pointer = y;
                    y = x;
                    x = pointer;
                }

                // 10  FIB-HEAP-LINK(H,y,x)
                link( y, x );

                // 11  A[d] <- NIL
                nodeSequence.set( degree, null );

                // 12  d <- d + 1
                degree++;
            }

            // 13  A[d] <- x
            nodeSequence.set( degree, x );

            x = x.getRight();
        }
        while ( x != minimumNode );

        // 14  min[H] <- NIL
        minimumNode = null;

        // 15  for i <- 0 to D(n[H])
        for ( FibonacciHeapNode<E> pointer : nodeSequence )
        {
            // 16 if A[i] != NIL
            if ( pointer != null )
            {
                // TODO 17            then add A[i] to the root list of H
                // TODO 18                 if min[H] = NIL or key[A[i]] &le; key[min[H]]
                // TODO 19                    then min[H] &larr; A[i]</pre>

                // FIXME this should be wrong
                add( pointer.getElement() );
            }
        }
    }

    /**
     * Implements the {@code FIB-HEAP-LINK(H, y, x)} function.
     *
     * <pre>FIB-HEAP-LINK(H, y, x)
     * 1  remove y from the root list of H
     * 2  make y a child of x, incrementing degree[x]
     * 3  mark[y]  FALSE</pre>
     *
     * @param y the node has to be removed from the root list
     * @param x the node has to to become fater of {@code y}
     */
    private void link( FibonacciHeapNode<E> y, FibonacciHeapNode<E> x )
    {
        // 1  remove y from the root list of H
        y.getLeft().setRight( y.getRight() );
        y.getRight().setLeft( y.getLeft() );

        // 2  make y a child of x, incrementing degree[x]
        x.setChild( y );
        y.setParent( x );
        x.incraeseDegree();

        // 3  mark[y] <- FALSE
        y.setMarked( false );
    }

    /**
     * Implements the {@code CUT(H,x,y)} function.
     *
     * <pre>CUT(H,x,y)
     * 1  remove x from the child list of y, decrementing degree[y]
     * 2  add x to the root list of H
     * 3  p[x] &larr; NIL
     * 4  mark[x] &larr; FALSE</pre>
     *
     * @param x the node has to be removed from {@code y} children
     * @param y the node has to be updated
     */
    private void cut( FibonacciHeapNode<E> x, FibonacciHeapNode<E> y )
    {
        // remove x from the child list of y, decrementing degree[y]
        x.getLeft().setRight( x.getRight() );
        x.getRight().setLeft( x.getLeft() );
        y.decraeseDegree();

        // add x to the root list of H
        // TODO!!!

        // p[x] <- NIL
        x.setParent( null );

        // mark[x] <- FALSE
        x.setMarked( false );
    }

    /**
     * Implements the {@code CASCADING-CUT(H,y)} function.
     *
     * <pre>CASCADING-CUT(H,y)
     * 1  z &larr; p[y]
     * 2  if z &ne; NIL
     * 3     then if mark[y] = FALSE
     * 4             then mark[y] &larr; TRUE
     * 5             else CUT(H,y,z)
     * 6                  CASCADING-CUT(H,z)</pre>
     *
     * @param y the target node to apply CASCADING-CUT
     */
    private void cascadingCut( FibonacciHeapNode<E> y )
    {
        // z <- p[y]
        FibonacciHeapNode<E> z = y.getParent();

        // if z != NIL
        if ( z != null )
        {
            // if mark[y] = FALSE
            if ( !y.isMarked() )
            {
                // then mark[y]  TRUE
                y.setMarked( true );
            }
            else
            {
                // else CUT(H,y,z)
                cut( y, z );
                // CASCADING-CUT(H,z)
                cascadingCut( z );
            }
        }
    }

    /**
     * The potential of Fibonacci heap {@code H} is then defined by
     * {@code t(H) + 2m(H)}.
     *
     * @return The potential of this Fibonacci heap.
     */
    public int potential()
    {
        return trees + 2 * markedNodes;
    }

    /**
     * Compare the given objects according to to the specified comparator if not null,
     * according to their natural ordering otherwise.
     *
     * @param o1 the first {@link FibonacciHeap} node to be compared
     * @param o2 the second {@link FibonacciHeap} node to be compared
     * @return a negative integer, zero, or a positive integer as the first argument is
     *         less than, equal to, or greater than the second
     */
    private int compare( FibonacciHeapNode<E> o1, FibonacciHeapNode<E> o2 )
    {
        if ( comparator != null )
        {
            return comparator.compare( o1.getElement(), o2.getElement() );
        }
        @SuppressWarnings( "unchecked" ) // it will throw a ClassCastException at runtime
        Comparable<? super E> o1Comparable = (Comparable<? super E>) o1.getElement();
        return o1Comparable.compareTo( o2.getElement() );
    }

}
