package com.spt.learningmanage.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.spt.learningmanage.model.entity.Project;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;

@Mapper
public interface ProjectMapper extends BaseMapper<Project> {

	@Update("""
			UPDATE project
			SET deleted_at = NULL,
			    is_delete = 0
			WHERE id = #{projectId}
			  AND tenant_id = #{tenantId}
			  AND user_id = #{userId}
			  AND deleted_at IS NOT NULL
			  AND deleted_at >= #{recoverAfter}
			""")
	int recoverOwnedProject(@Param("tenantId") Long tenantId,
							 @Param("userId") Long userId,
							 @Param("projectId") Long projectId,
							 @Param("recoverAfter") LocalDateTime recoverAfter);
}
