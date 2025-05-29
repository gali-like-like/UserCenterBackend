package com.galilikelike.model.dto;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.galilikelike.model.vo.UserVo;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@NoArgsConstructor
@AllArgsConstructor
@Data
public class PageDto {
    @NotNull(message = "当前页不能为空")
    @Min(value = 1,message = "必须从第一页起步")
    private Integer current;

    @NotNull(message = "分页大小不能为空")
    @Min(value = 10,message = "分页大小不能小于10页")
    private Integer pageSize;
}
