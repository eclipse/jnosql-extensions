package org.jnosql.artemis.graph;

import java.util.Objects;

/**
 * The default implementation of {@link GraphEntityPrePersist}
 */
class DefaultGraphEntityPrePersist implements GraphEntityPrePersist {

    private final ArtemisVertex entity;

    DefaultGraphEntityPrePersist(ArtemisVertex entity) {
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
        if (!(o instanceof DefaultGraphEntityPrePersist)) {
            return false;
        }
        DefaultGraphEntityPrePersist that = (DefaultGraphEntityPrePersist) o;
        return Objects.equals(entity, that.entity);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(entity);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("DefaultGraphEntityPrePersist{");
        sb.append("entity=").append(entity);
        sb.append('}');
        return sb.toString();
    }
}
