package org.neo4j.dih.bean;

import org.codehaus.jackson.map.ObjectMapper;
import org.neo4j.graphdb.QueryStatistics;

import java.io.IOException;

/**
 * Bean with all statistics of the script.
 *
 * @author bsimard
 * @version $Id: $Id
 */
public class ScriptStatistics implements QueryStatistics {

    /**
     * List of all statistic.
     */
    private int nodesCreated = 0;
    private int nodesDeleted = 0;
    private int relationshipsCreated = 0;
    private int relationshipsDeleted = 0;
    private int propertiesSet = 0;
    private int labelsAdded = 0;
    private int labelsRemoved = 0;
    private int indexesAdded = 0;
    private int indexesRemoved = 0;
    private int constraintsAdded = 0;
    private int constraintsRemoved = 0;

    /**
     * Constructor.
     */
    public ScriptStatistics() {
        super();
    }

    /**
     * Method to sum the content of the QueryStatistics.
     *
     * @param stats a {@link org.neo4j.graphdb.QueryStatistics} object.
     */
    public void add(QueryStatistics stats) {
        this.nodesCreated += stats.getNodesCreated();
        this.nodesDeleted += stats.getNodesDeleted();
        this.relationshipsCreated += stats.getRelationshipsCreated();
        this.relationshipsDeleted += stats.getRelationshipsDeleted();
        this.propertiesSet += stats.getPropertiesSet();
        this.labelsAdded += stats.getLabelsAdded();
        this.labelsRemoved += stats.getLabelsRemoved();
        this.indexesAdded += stats.getIndexesAdded();
        this.indexesRemoved += stats.getIndexesRemoved();
    }

    /**
     * Convert this bean into JSON.
     *
     * @return The JSON representation of this bean
     * @throws java.io.IOException if any.
     */
    public String toJson() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(this);
    }

    /** {@inheritDoc} */
    @Override
    public int getNodesCreated() {
        return nodesCreated;
    }

    /** {@inheritDoc} */
    @Override
    public int getNodesDeleted() {
        return nodesDeleted;
    }

    /** {@inheritDoc} */
    @Override
    public int getRelationshipsCreated() {
        return relationshipsCreated;
    }

    /** {@inheritDoc} */
    @Override
    public int getRelationshipsDeleted() {
        return relationshipsDeleted;
    }

    /** {@inheritDoc} */
    @Override
    public int getPropertiesSet() {
        return propertiesSet;
    }

    /** {@inheritDoc} */
    @Override
    public int getLabelsAdded() {
        return labelsAdded;
    }

    /** {@inheritDoc} */
    @Override
    public int getLabelsRemoved() {
        return labelsRemoved;
    }

    /** {@inheritDoc} */
    @Override
    public int getIndexesAdded() {
        return indexesAdded;
    }

    /** {@inheritDoc} */
    @Override
    public int getIndexesRemoved() {
        return indexesRemoved;
    }

    /** {@inheritDoc} */
    @Override
    public int getConstraintsAdded() {
        return constraintsAdded;
    }

    /** {@inheritDoc} */
    @Override
    public int getConstraintsRemoved() {
        return constraintsRemoved;
    }

    /** {@inheritDoc} */
    @Override
    public boolean containsUpdates() {
        return false;
    }
}
