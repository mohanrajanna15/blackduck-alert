package com.synopsys.integration.alert.database.repository.descriptor;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import com.synopsys.integration.alert.database.entity.descriptor.FieldContextRelation;
import com.synopsys.integration.alert.database.relation.key.FieldContextRelationPK;

@Component
public interface FieldContextRepository extends JpaRepository<FieldContextRelation, FieldContextRelationPK> {
    List<FieldContextRelation> findByFieldId(final Long fieldId);

    List<FieldContextRelation> findByContextId(final Long contextId);
}
