package nl.vpro.newrelic;

import com.newrelic.api.agent.NewRelic;

import nl.vpro.domain.media.TransactionUUID;

/**
 * @author Michiel Meeuwissen
 * @since 4.7
 */
public class NewRelicTransactionUUIDConsumer implements TransactionUUID.TransactionUUIDConsumer {
    public NewRelicTransactionUUIDConsumer() {
        
    }
    @Override
    public void accept(String uuid) {
        //NewRelic.setTransactionName("TransactionUUID", uuid.toString());
        NewRelic.addCustomParameter("TransactionUUID", uuid);
    }
}
