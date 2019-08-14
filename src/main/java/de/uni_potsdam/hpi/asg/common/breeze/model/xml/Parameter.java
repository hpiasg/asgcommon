package de.uni_potsdam.hpi.asg.common.breeze.model.xml;

/*
 * Copyright (C) 2012 - 2018 Norman Kluge
 * 
 * This file is part of ASGcommon.
 * 
 * ASGcommon is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * ASGcommon is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with ASGcommon.  If not, see <http://www.gnu.org/licenses/>.
 */

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlEnum;

@XmlAccessorType(XmlAccessType.NONE)
public class Parameter {
    @XmlEnum
    public enum ParameterType {
        name, // Name of variable -- BrzVariable
        width, // Width of all signals -- BrzCallMux, BrzFetch, BrzConstant, BrzCaseFetch
        operator, // -- BrzBinaryFunc, BrzBinaryFuncConstR
        value, // -- BrzConstant
        port_count, // -- BrzDecisionWait
        index_width, // -- BrzCaseFetch
        low_index, // BrzSlice
        guard_count, // BrzBar

        input_count, // -- BrzCallMux, BrzCaseFetch, BrzCombineEqual, BrzEncode
        input_width, // -- BrzVariable, BrzCase, BrzFalseVariable, BrzAdapt, BrzCombineEqual
        input_signed, // -- BrzAdapt

        inputA_width, // -- BrzBinaryFunc, BrzBinaryFuncConstR, BrzCombine
        inputA_signed, // -- BrzBinaryFunc, BrzBinaryFuncConstR

        inputB_width, // -- BrzBinaryFunc, BrzBinaryFuncConstR, BrzCombine
        inputB_signed, // -- BrzBinaryFunc, BrzBinaryFuncConstR
        inputB_value, // -- BrzBinaryFuncConstR

        outputA_width, // BrzSplit
        outputB_width, // BrzSplit

        output_width, // -- BrzBinaryFunc, BrzBinaryFuncConstR, BrzCombine, BrzAdapt, BrzCombineEqual, BrzEncode
        output_signed, // -- BrzBinaryFunc, BrzBinaryFuncConstR, BrzAdapt
        output_count, // -- BrzVariable, BrzCase, BrzFalseVariable, BrzWireFork

        var_spec, // Outputspec of Variable -- BrzVariable, BrzFalseVariable
        fetch_spec, // NYI -- BrzFetch, BrzCaseFetch
        case_spec, // NYI -- BrzCase
        encode_spec, // NYI -- BrzEncode
        sequence_spec
    }

    @XmlAttribute(name = "id", required = true)
    private int           id;
    @XmlAttribute(name = "type", required = true)
    private ParameterType type;

    public int getId() {
        return id;
    }

    public ParameterType getType() {
        return type;
    }
}
