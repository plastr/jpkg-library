#!/usr/local/bin/ruby
require 'date'

# known to work on dpkg-architecture version 1.13.25
CMD="dpkg-architecture"

# padding
PAD="    "
COL1="%s"
COL2="#{PAD}%s"
COL3="#{PAD}#{PAD}%s"

version = `#{CMD} --version`.split("\n")[0]
now = DateTime::now()

# turn an architecture string into an enum field
def enum (arch, last=false)
  field = arch.upcase
  field = field.tr("-", "_")
  return "#{field} (\"#{arch}\"),\n" unless last 
  return "#{field} (\"#{arch}\");\n" if last
end

# returns a string as an inline java comment
def comment (comment)
  return "/** #{comment} */\n" 
end

# header
printf COL1, "package com.threerings.jpkg.debian;\n"
printf COL1, "/**\n"
printf COL1, " * Known Debian architectures used by the Debian packaging system.\n"
printf COL1, " * Generated from #{version} on #{now}.\n"
printf COL1, " * @see <a href=\"http://www.debian.org/doc/debian-policy/ch-controlfields.html#s-f-Architecture\">Debian Policy Manual</a>\n"
printf COL1, " */\n"
printf COL1, "public enum DebianArchitectures\n"
printf COL1, "{\n"

# any and all and source are always defined
printf COL2, comment("Indicates a package available for building on any architecture.")
printf COL2, enum("any")
printf COL2, comment("Indicates an architecture-independent package.")
printf COL2, enum("all")
printf COL2, comment("Indicates a source package.")
printf COL2, enum("source")

print "\n"
printf COL2, comment("fields generated from dpkg--architecture -L output")
architectures = `#{CMD} -L`.split
len = architectures.length
architectures.each { |arch|
  arch.chomp!
  printf COL2, comment("The #{arch} architecture.")
  printf COL2, enum(arch) unless len == 1
  # use a ; for the last element
  printf COL2, enum(arch, true) if len == 1
  len -= 1
}

# enum constructor and rest of class
print "\n"
printf COL2, "DebianArchitectures (String name)\n"
printf COL2, "{\n"
printf COL3, "_name = name;\n"
printf COL2, "}\n"
print "\n"
printf COL2, comment("Returns the string name for this architecture")
printf COL2, "public String getName ()\n"
printf COL2, "{\n"
printf COL3, "return _name;\n"
printf COL2, "}\n"
print "\n"
printf COL2, "private final String _name;\n"
printf COL1, "}\n"
