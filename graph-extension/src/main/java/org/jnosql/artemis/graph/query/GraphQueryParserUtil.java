package org.jnosql.artemis.graph.query;

import org.apache.tinkerpop.gremlin.process.traversal.P;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.jnosql.artemis.DynamicQueryException;
import org.jnosql.artemis.reflection.ClassRepresentation;

final class GraphQueryParserUtil {

    static final String AND = "And";
    static final String EMPTY = "";

    static final String ORDER_BY = "OrderBy";
    private static final String BETWEEN = "Between";
    private static final String LESS_THAN = "LessThan";
    private static final String GREATER_THAN = "GreaterThan";
    private static final String LESS_THAN_EQUAL = "LessEqualThan";
    private static final String GREATER_THAN_EQUAL = "GreaterEqualThan";

    private GraphQueryParserUtil() {
    }

    static GraphTraversal<?, ?> feedTraversal(String token,
                                              int index,
                                              Object[] args,
                                              String methodName,
                                              ClassRepresentation representation,
                                              GraphTraversal<?, ?> traversal) {

        boolean containsBetween = token.contains(BETWEEN);

        if (containsBetween) {
            checkContents(index, args.length, 2, methodName);
            String name = getName(token, representation).replace(BETWEEN, EMPTY);
            return traversal.has(name, P.between(args[index], args[++index]));
        }

        checkContents(index, args.length, 1, methodName);

        if (token.contains(LESS_THAN)) {
            String name = getName(token, representation).replace(LESS_THAN, EMPTY);
            return traversal.has(name, P.lt(args[index]));
        }

        if (token.contains(GREATER_THAN)) {
            String name = getName(token, representation).replace(GREATER_THAN, EMPTY);
            return traversal.has(name, P.gt(args[index]));
        }

        if (token.contains(LESS_THAN_EQUAL)) {
            String name = getName(token, representation).replace(LESS_THAN_EQUAL, EMPTY);
            return traversal.has(name, P.lte(args[index]));
        }

        if (token.contains(GREATER_THAN_EQUAL)) {
            String name = getName(token, representation).replace(GREATER_THAN_EQUAL, EMPTY);
            return traversal.has(name, P.gte(args[index]));
        }


        String name = getName(token, representation);
        return traversal.has(name, args[index]);
    }


    static int and(Object[] args,
                   int index,
                   String token,
                   String methodName,
                   ClassRepresentation representation,
                   GraphTraversal<?, ?> traversal) {
        String field = token.replace(AND, EMPTY);
        feedTraversal(field, index, args, methodName, representation, traversal);

        if (token.contains(BETWEEN)){
            return index + 2;
        } else{
            return ++index;
        }
    }


    private static void checkContents(int index, int argSize, int required, String method) {
        if ((index + required) <= argSize) {
            return;
        }
        throw new DynamicQueryException(String.format("There is a missed argument in the method %s",
                method));
    }


    private static String getName(String token, ClassRepresentation representation) {
        return representation.getColumnField(String.valueOf(Character.toLowerCase(token.charAt(0)))
                .concat(token.substring(1)));
    }

}
