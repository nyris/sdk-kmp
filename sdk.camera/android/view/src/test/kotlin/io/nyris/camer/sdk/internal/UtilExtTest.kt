package io.nyris.camer.sdk.internal

import io.nyris.sdk.camera.core.CaptureMode
import io.nyris.sdk.camera.core.CaptureModeEnum
import io.nyris.sdk.camera.core.CompressionFormat
import io.nyris.sdk.camera.core.CompressionFormatEnum
import io.nyris.sdk.camera.core.FocusMode
import io.nyris.sdk.camera.core.FocusModeEnum
import io.nyris.sdk.camera.internal.byteToKb
import io.nyris.sdk.camera.internal.byteToMb
import io.nyris.sdk.camera.internal.millisToSeconds
import io.nyris.sdk.camera.internal.required
import io.nyris.sdk.camera.internal.round
import io.nyris.sdk.camera.internal.toCaptureMode
import io.nyris.sdk.camera.internal.toCompressionFormat
import io.nyris.sdk.camera.internal.toFocusMode
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

class UtilExtTest {
    @Test
    fun `require should throw exception when field is null`() {
        val field: String? = null

        assertThrows<IllegalArgumentException> {
            field.required()
        }
    }

    @Test
    fun `round should float to 2 digits after dot`() {
        val value = 1.3533288F

        assertEquals(1.35F, value.round())
    }

    @Test
    fun `byteToKb should convert by to kb`() {
        val byte = 1024

        assertEquals(1.02F, byte.byteToKb())
    }

    @Test
    fun `byteToMb should convert by to mb`() {
        val byte = 1024 * 1024

        assertEquals(1.05F, byte.byteToMb())
    }

    @Test
    fun `millisToSeconds should convert millis to second`() {
        val millis = 98379L

        assertEquals(98.38F, millis.millisToSeconds())
    }

    @ParameterizedTest(name = "toCaptureMode should convert int to the correct enum[{0}]")
    @MethodSource("getCaptureModeTestData")
    fun `toCaptureMode should convert int to the correct enum`(
        @CaptureMode captureMode: Int,
        expected: CaptureModeEnum,
    ) {
        assertEquals(expected, captureMode.toCaptureMode())
    }

    @ParameterizedTest(name = "toFocusMode should convert int to the correct enum[{0}]")
    @MethodSource("getFocusModeTestData")
    fun `toFocusMode should convert int to the correct enum`(
        @FocusMode focusMode: Int,
        expected: FocusModeEnum,
    ) {
        assertEquals(expected, focusMode.toFocusMode())
    }

    @ParameterizedTest(name = "toCompressionFormat should convert int to the correct enum[{0}]")
    @MethodSource("getCompressionFormatTestData")
    fun `toCompressionFormat should convert int to the correct enum`(
        @CompressionFormat compressionFormat: Int,
        expected: CompressionFormatEnum,
    ) {
        assertEquals(expected, compressionFormat.toCompressionFormat())
    }

    companion object {
        @JvmStatic
        fun getCaptureModeTestData() = listOf(
            Arguments.of(0, CaptureModeEnum.Screenshot),
            Arguments.of(1, CaptureModeEnum.Lens),
            Arguments.of(2, CaptureModeEnum.Barcode),
            Arguments.of(-76381, CaptureModeEnum.Screenshot),
        )

        @JvmStatic
        fun getFocusModeTestData() = listOf(
            Arguments.of(0, FocusModeEnum.Automatic),
            Arguments.of(1, FocusModeEnum.Manual),
            Arguments.of(-76381, FocusModeEnum.Automatic),
        )

        @JvmStatic
        fun getCompressionFormatTestData() = listOf(
            Arguments.of(0, CompressionFormatEnum.WebP),
            Arguments.of(1, CompressionFormatEnum.Jpeg),
            Arguments.of(-76381, CompressionFormatEnum.WebP),
        )
    }
}
