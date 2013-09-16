package simpledb;

import java.io.Serializable;
import java.util.*;

/**
 * TupleDesc describes the schema of a tuple.
 */
public class TupleDesc implements Serializable {

    /**
     * A help class to facilitate organizing the information of each field
     * */
    public static class TDItem implements Serializable {

        private static final long serialVersionUID = 1L;

        /**
         * The type of the field
         * */
        Type fieldType;
        
        /**
         * The name of the field
         * */
        String fieldName;

        public TDItem(Type t, String n) {
            this.fieldName = n;
            this.fieldType = t;
        }

        public String toString() {
            return fieldName + "(" + fieldType + ")";
        }
    }

    /**
     * @return
     *        An iterator which iterates over all the field TDItems
     *        that are included in this TupleDesc
     * */
    public Iterator<TDItem> iterator() {
        // some code goes here
        return TDItemArray.iterator();
    }

    private ArrayList<TDItem> TDItemArray = new ArrayList<TDItem>();
    private static final long serialVersionUID = 1L;

    /**
     * Create a new TupleDesc with typeAr.length fields with fields of the
     * specified types, with associated named fields.
     * 
     * @param typeAr
     *            array specifying the number of and types of fields in this
     *            TupleDesc. It must contain at least one entry.
     * @param fieldAr
     *            array specifying the names of the fields. Note that names may
     *            be null.
     */
    public TupleDesc(Type[] typeAr, String[] fieldAr) {
        // some code goes here
        assert typeAr.length >= 1 : "Must Be of at least length 1";
        assert typeAr.length == fieldAr.length : "fields and types must be of same length";
        
        for (int i=0; i<typeAr.length; i++) { 
            TDItemArray.add(new TDItem(typeAr[i], fieldAr[i]));
        }
    }

    /**
     * Constructor. Create a new tuple desc with typeAr.length fields with
     * fields of the specified types, with anonymous (unnamed) fields.
     * 
     * @param typeAr
     *            array specifying the number of and types of fields in this
     *            TupleDesc. It must contain at least one entry.
     */
    public TupleDesc(Type[] typeAr) {
        // some code goes here
        for (Type type : typeAr) {
            TDItemArray.add(new TDItem(type, null));
        }
    }

    /**
     * @return the number of fields in this TupleDesc
     */
    public int numFields() {
        // some code goes here
        return TDItemArray.size();
    }

    /* Helper method for getting element of index i */
    private TDItem getTDItem(int i) throws NoSuchElementException {
        try {
            return TDItemArray.get(i);
        } catch (IndexOutOfBoundsException IOOB_e) {
            System.err.println("Invalid Index Exception: " + IOOB_e.getMessage());
            throw new NoSuchElementException();
        }
    }

    /**
     * Gets the (possibly null) field name of the ith field of this TupleDesc.
     * 
     * @param i
     *            index of the field name to return. It must be a valid index.
     * @return the name of the ith field
     * @throws NoSuchElementException
     *             if i is not a valid field reference.
     */
    public String getFieldName(int i) throws NoSuchElementException {
        return getTDItem(i).fieldName;  
    }

    /**
     * Gets the type of the ith field of this TupleDesc.
     * 
     * @param i
     *            The index of the field to get the type of. It must be a valid
     *            index.
     * @return the type of the ith field
     * @throws NoSuchElementException
     *             if i is not a valid field reference.
     */
    public Type getFieldType(int i) throws NoSuchElementException {
        return getTDItem(i).fieldType;
    }

    /**
     * Find the index of the field with a given name.
     * 
     * @param name
     *            name of the field.
     * @return the index of the field that is first to have the given name.
     * @throws NoSuchElementException
     *             if no field with a matching name is found.
     */
    public int fieldNameToIndex(String name) throws NoSuchElementException {
        // some code goes here
        try {
            for (int i=0; i<TDItemArray.size(); i++) {
                if (name.equals(TDItemArray.get(i).fieldName))
                    return i;
            }
            throw new NoSuchElementException();
        }
        catch (NullPointerException NP_e) {
            System.err.println("Null Pointer: " + NP_e.getMessage());
            throw new NoSuchElementException();
        }
    }

    /**
     * @return The size (in bytes) of tuples corresponding to this TupleDesc.
     *         Note that tuples from a given TupleDesc are of a fixed size.
     */
    public int getSize() {
        // some code goes here
        return this.numFields() * 4;
    }

    /**
     * Merge two TupleDescs into one, with td1.numFields + td2.numFields fields,
     * with the first td1.numFields coming from td1 and the remaining from td2.
     *  
     * @param td1
     *            The TupleDesc with the first fields of the new TupleDesc
     * @param td2
     *            The TupleDesc with the last fields of the TupleDesc
     * @return the new TupleDesc
     */
    public static TupleDesc merge(TupleDesc td1, TupleDesc td2) {
        // some code goes here
        Type[]  td_typeAr  = new Type[td1.numFields() + td2.numFields()];
        String[] td_fieldAr = new String[td1.numFields() + td2.numFields()];

        Iterator<TDItem> td1_iter = td1.iterator();
        Iterator<TDItem> td2_iter = td2.iterator();

        for (int i=0; td1_iter.hasNext() && i<td1.numFields(); i++) {
            TDItem curr = td1_iter.next();
            td_typeAr[i] = curr.fieldType;
            td_fieldAr[i] = curr.fieldName;
        }
        for (int i=td1.numFields(); i<td1.numFields()+td2.numFields() && td2_iter.hasNext(); i++) {
            TDItem curr = td2_iter.next();
            td_typeAr[i] = curr.fieldType;
            td_fieldAr[i] = curr.fieldName;
        }
        return new TupleDesc(td_typeAr, td_fieldAr);
    }

    /**
     * Compares the specified object with this TupleDesc for equality. Two
     * TupleDescs are considered equal if they are the same size and if the n-th
     * type in this TupleDesc is equal to the n-th type in td.
     * 
     * @param o
     *            the Object to be compared for equality with this TupleDesc.
     * @return true if the object is equal to this TupleDesc.
     */
    public boolean equals(Object o) {
        // some code goes here
        if (o == null)
            return false;
        if (!(o instanceof TupleDesc)) 
            return false;
        TupleDesc obj = (TupleDesc) o;
        Iterator<TDItem> iter1 = this.iterator();
        Iterator<TDItem> iter2 = obj.iterator();
        while (iter1.hasNext() && iter2.hasNext()) {
            Type curr1 = iter1.next().fieldType;
            Type curr2 = iter2.next().fieldType;
            if (curr1.getLen() != curr2.getLen())
                return false;
        }
        return !(iter1.hasNext() || iter2.hasNext());
    }

    public int hashCode() {
        // If you want to use TupleDesc as keys for HashMap, implement this so
        // that equal objects have equals hashCode() results
        throw new UnsupportedOperationException("unimplemented");
    }

    /**
     * Returns a String describing this descriptor. It should be of the form
     * "fieldType[0](fieldName[0]), ..., fieldType[M](fieldName[M])", although
     * the exact format does not matter.
     * 
     * @return String describing this descriptor.
     */
    public String toString() {
        // some code goes here
        String s = new String();
        Iterator<TDItem> iter = this.iterator();
        while (iter.hasNext()){
            TDItem curr = iter.next();
            s += curr.toString() + ", ";
        }
        return s;   
    }
}
