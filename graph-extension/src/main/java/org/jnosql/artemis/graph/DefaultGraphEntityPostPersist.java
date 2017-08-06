package org.jnosql.artemis.graph;

import org.jnosql.diana.api.column.ColumnEntity;

import java.util.Objects;

class DefaultGraphEntityPostPersist implements GraphEntityPostPersist {

    private final ArtemisVertex entity;

    DefaultGraphEntityPostPersist(ArtemisVertex entity) {
        this.entity = entity;
    }

    @Override
    public ArtemisVertex getEntity() {
        return entity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DefaultGraphEntityPostPersist)) {
            return false;
        }
        DefaultGraphEntityPostPersist that = (DefaultGraphEntityPostPersist) o;
        return Objects.equals(entity, that.entity);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(entity);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("DefaultGraphEntityPostPersist{");
        sb.append("entity=").append(entity);
        sb.append('}');
        return sb.toString();
    }
}
