@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package types.rust

typealias Exports = Record<String, dynamic /* Function<*> | Global | Memory | Table */>

typealias ModuleImports = Record<String, dynamic /* Function<*> | Global | Memory | Table | Number */>

typealias Imports = Record<String, ModuleImports>