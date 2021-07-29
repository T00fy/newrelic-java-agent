/*
 *
 *  * Copyright 2020 New Relic Corporation. All rights reserved.
 *  * SPDX-License-Identifier: Apache-2.0
 *
 */

package graphql;

import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.weaver.MatchType;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import com.nr.instrumentation.graphql.GraphQLTransactionName;
import graphql.execution.ExecutionContextBuilder;
import graphql.language.Document;

@Weave(originalName = "graphql.execution.ExecutionContextBuilder", type = MatchType.ExactClass)
public class ExecutionContextBuilder_Instrumentation {

    @Trace
    public ExecutionContextBuilder document(Document document) {
        System.out.println("ExecutionContextBuilder.document()");
        String transactionName = GraphQLTransactionName.from(document);
        System.out.println("Setting transaction name to [" + transactionName +"]");
        NewRelic.setTransactionName("GraphQL", transactionName);

        /* Currently, this is the first place we can tap into the Document, which contains a parsed query.
        By tracing it and renaming this tracer, the DT UI should look like the following:

        GraphQL.executeAsync...
           GraphQL + transactionName...
              resolver + field 1...
              resolver + field 2...
              ...
         */

        System.out.println("Setting tracer name to [" + transactionName +"]");
        NewRelic.getAgent().getTracedMethod().setMetricName("GraphQL/" + transactionName);
        return Weaver.callOriginal();
    }
}