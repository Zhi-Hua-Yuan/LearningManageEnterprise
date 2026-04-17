package com.spt.learningmanage.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.spt.learningmanage.model.entity.Milestone;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;

@Mapper
public interface MilestoneMapper extends BaseMapper<Milestone> {

	@Update("""
			UPDATE milestone
			SET is_delete = 1,
			    deleted_at = #{deleteTime},
			    delete_source = #{deleteSource}
			WHERE id = #{id}
			  AND tenant_id = #{tenantId}
			  AND user_id = #{userId}
			  AND is_delete = 0
			""")
	int softDeleteOwnedMilestone(@Param("tenantId") Long tenantId,
							 @Param("userId") Long userId,
							 @Param("id") Long id,
							 @Param("deleteTime") LocalDateTime deleteTime,
							 @Param("deleteSource") Integer deleteSource);

	@Update("""
			UPDATE milestone
			SET is_delete = 0,
			    deleted_at = NULL,
			    delete_source = 0
			WHERE tenant_id = #{tenantId}
			  AND user_id = #{userId}
			  AND project_id = #{projectId}
			  AND is_delete = 1
			  AND delete_source = 2
			""")
	int recoverByProjectId(@Param("tenantId") Long tenantId,
						 @Param("userId") Long userId,
						 @Param("projectId") Long projectId);
}
