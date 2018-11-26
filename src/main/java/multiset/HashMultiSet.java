package multiset;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;

/**
 * <p>A MultiSet models a data structure containing elements along with their frequency count i.e., </p>
 * <p>the number of times an element is present in the set.</p>
 * <p>HashMultiSet is a Map-based concrete implementation of the MultiSet concept.</p>
 * 
 * <p>MultiSet a = <{1:2}, {2:2}, {3:4}, {10:1}></p>
 * */
public final class HashMultiSet<T, V extends  Number> {

	private HashMap<T,V> hash;
	
	/**
	 * Sole constructor of the class.
	 **/
	public HashMultiSet() {

		hash = new HashMap<T, V>();

	}
	
	
	/**
	 * If not present, adds the element to the data structure, otherwise 
	 * simply increments its frequency.
	 * 
	 * @param t T: element to include in the multiset
	 * 
	 * @return V: frequency count of the element in the multiset
	 * */	
	public V addElement(T t) {
		Integer i = 1;

		V currentValue = hash.putIfAbsent(t, (V) i);

		if(currentValue == null)
			return (V) i;
		Integer updatedValue = (Integer) currentValue + i;
		hash.put(t,(V)updatedValue);
		return (V)updatedValue;
	}

	/**
	 * Check whether the elements is present in the multiset.
	 * 
	 * @param t T: element
	 * 
	 * @return V: true if the element is present, false otherwise.
	 * */	
	public boolean isPresent(T t) {
		if(hash.get(t) == null)
			return true;
		else
			return false;
	}
	
	/**
	 * @param t T: element
	 * @return V: frequency count of parameter t ('0' if not present)
	 * */
	public V getElementFrequency(T t) {
		Integer returnValue = (Integer) hash.get(t);
		if( returnValue != null)
			return (V) returnValue;
		else
			return (V)(Integer) 0;
	}
	
	
	/**
	 * Builds a multiset from a source data file. The source data file contains
	 * a number comma separated elements. 
	 * Example_1: ab,ab,ba,ba,ac,ac -->  <{ab:2},{ba:2},{ac:2}>
	 * Example 2: 1,2,4,3,1,3,4,7 --> <{1:2},{2:1},{3:2},{4:2},{7:1}>
	 * 
	 * @param source Path: source of the multiset
	 * */
	public void buildFromFile(Path source) throws IOException {
		try(Stream<String> in = Files.lines(source)){
			in.forEach( line -> {
				String[] string = line.split(",");
				Arrays.stream(string).forEach(element -> this.addElement((T)element.trim()));

			}
		);
		}catch (IOException e){
			System.out.println("Failed to build MultiSet from " + source.toString());
		}
		
	}

	/**
	 * Same as before with the difference being the source type.
	 * @param source List<T>: source of the multiset
	 * */
	public void buildFromCollection(List<? extends T> source) {
		source.stream().forEach(element -> this.addElement(element));
	}
	
	/**
	 * Produces a linearized, unordered version of the MultiSet data structure.
	 * Example: <{1:2},{2:1}, {3:3}> -> 1 1 2 3 3 3 3
	 * 
	 * @return List<T>: linearized version of the multiset represented by this object.
	 */
	public List<T> linearize() {
		ArrayList<T> toReturn= new ArrayList<T>();
		hash.entrySet().stream().forEach(entry ->{
			Integer total = (Integer)entry.getValue();
			for(Integer i =0; i < total ; i++){
				toReturn.add(entry.getKey());
			}
		});
		return toReturn;
	}
	
	
}
