package com.omicronlab.avro.phonetic.perst.models;

import org.garret.perst.FieldIndex;
import org.garret.perst.Persistent;
import org.garret.perst.Storage;

public class AvroRootClass extends Persistent {
	public FieldIndex<WordPersistentClass> intKeyIndex;
	public FieldIndex<PatternPersistentClass> strKeyIndex;

	public AvroRootClass(Storage db) {
		super(db);
		// Parameters: class for which index is defined, name of indexed field,
		// unique index
		intKeyIndex = db.<WordPersistentClass> createFieldIndex(WordPersistentClass.class, "intKey", true);
		strKeyIndex = db.<PatternPersistentClass> createFieldIndex(PatternPersistentClass.class, "strKey", true);
	}

	public AvroRootClass() {
		// Default constructor is needed for Perst to be able to instantiate
		// instances of loaded
		// objects. In case of using Sun JVM, it can be skipped
	}
}
