JFLAGS = -g
JC = javac
.SUFFIXES: .java .class
.java.class:
        $(JC) $(JFLAGS) $*.java

CLASSES = \
        MyUtils.java \
        DataHolder.java \
        Joiner.java \
        AprioriTid.java \
        AssociationRule.java \
        RulesMiner.java \
        Main.java

default: classes

classes: $(CLASSES:.java=.class)

clean:
        $(RM) *.class