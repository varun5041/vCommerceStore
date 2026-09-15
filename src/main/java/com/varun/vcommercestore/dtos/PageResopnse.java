package com.varun.vcommercestore.dtos;

import lombok.*;

import java.util.List;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PageResopnse<T>{
    private List<T> content;
    private int pagesize;
    private int ppagenumber;
    private long totalElements;
    private int totalpages;
    private boolean lastpage;
}
