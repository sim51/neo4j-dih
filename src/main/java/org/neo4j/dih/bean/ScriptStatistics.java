package org.neo4j.dih.bean;

import org.codehaus.jackson.map.ObjectMapper;
import org.neo4j.graphdb.QueryStatistics;

import java.io.IOException;

/**
 * Bean with all statistics of the script.
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
     * @param stats
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
     * @throws IOException
     */
    public String toJson() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(this);
    }

    @Override
    public int getNodesCreated() {
        return nodesCreated;
    }

    @Override
    public int getNodesDeleted() {
        return nodesDeleted;
    }

    @Override
    public int getRelationshipsCreated() {
        return relationshipsCreated;
    }

    @Override
    public int getRelationshipsDeleted() {
        return relationshipsDeleted;
    }

    @Override
    public int getPropertiesSet() {
        return propertiesSet;
    }

    @Override
    public int getLabelsAdded() {
        return labelsAdded;
    }

    @Override
    public int getLabelsRemoved() {
        return labelsRemoved;
    }

    @Override
    public int getIndexesAdded() {
        return indexesAdded;
    }

    @Override
    public int getIndexesRemoved() {
        return indexesRemoved;
    }

    @Override
    public int getConstraintsAdded() {
        return constraintsAdded;
    }

    @Override
    public int getConstraintsRemoved() {
        return constraintsRemoved;
    }

    @Override
    public boolean containsUpdates() {
        return false;
    }
}
