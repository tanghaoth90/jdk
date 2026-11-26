/*
 * Copyright (c) 2026, ByteDance and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

package org.openjdk.bench.vm.compiler;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import jdk.internal.misc.Unsafe;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 1, time = 10)
@Measurement(iterations = 1, time = 60)
@Fork(value = 1, jvmArgsAppend = {"--add-exports=java.base/jdk.internal.misc=ALL-UNNAMED"})
@State(Scope.Thread)
public class ArrayAllocationNoZero {
    private static final Unsafe UNSAFE = Unsafe.getUnsafe();

    @Param({"4194304"})
    public int size;

    private String first;
    private String second;
    private int number = 123;
    private char suffix = ';';

    @Setup
    public void setup() {
        first = "a".repeat(size);
        second = "b".repeat(size);
    }

    @Benchmark
    public byte[] allocateUninitializedAndFill() {
        byte[] array = (byte[]) UNSAFE.allocateUninitializedArray(byte.class, size);
        Arrays.fill(array, (byte) 0x5A);
        return array;
    }

    @Benchmark
    public String appendStrings() {
        return new StringBuilder().append(first).append(second).toString();
    }

    @Benchmark
    public String appendMixed() {
        return new StringBuilder().append(first).append(number).append(suffix).toString();
    }
}
