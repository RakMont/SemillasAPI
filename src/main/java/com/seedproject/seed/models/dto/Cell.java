package com.seedproject.seed.models.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class Cell {
    public CellHeader cellHeader;
    public CellProperty cellProperty;
    public List<CellContent> contents;

    public Cell(CellHeader cellHeader, CellProperty cellProperty, List<CellContent> contents) {
        this.cellHeader = cellHeader;
        this.cellProperty = cellProperty;
        this.contents = contents;
    }
}
