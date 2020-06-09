package edu.iis.mto.testreactor.dishwasher;

import static edu.iis.mto.testreactor.dishwasher.Status.SUCCESS;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import edu.iis.mto.testreactor.dishwasher.engine.Engine;
import edu.iis.mto.testreactor.dishwasher.pump.WaterPump;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import org.hamcrest.Matchers;

@ExtendWith(MockitoExtension.class)
public class DishWasherTest {
    @Mock
    private WaterPump waterPump;
    @Mock
    private Engine engine;
    @Mock
    private DirtFilter dirtFilter;
    @Mock
    private Door door;

    private DishWasher dishWasher;

    private ProgramConfiguration properProgramConfigurationWithRinseProgramHalfLevelFillWithNoTablets;
    private ProgramConfiguration properProgramConfigurationWithRinseProgramHalfLevelFillWithTablets;

    @Test
    public void itCompiles() {
        assertThat(true, Matchers.equalTo(true));
    }

    @BeforeEach
    void setUp() {
        dishWasher = new DishWasher(waterPump, engine, dirtFilter, door);

        properProgramConfigurationWithRinseProgramHalfLevelFillWithNoTablets = ProgramConfiguration.builder()
                .withProgram(WashingProgram.RINSE)
                .withFillLevel(FillLevel.HALF)
                .withTabletsUsed(false)
                .build();

        properProgramConfigurationWithRinseProgramHalfLevelFillWithTablets = ProgramConfiguration.builder()
                .withProgram(WashingProgram.RINSE)
                .withFillLevel(FillLevel.HALF)
                .withTabletsUsed(true)
                .build();
    }

    @Test
    public void properProgramWithDoorsClosedWithNoTabletsWithRinseProgram_success() {
        when(door.closed()).thenReturn(true);

        RunResult result = dishWasher.start(properProgramConfigurationWithRinseProgramHalfLevelFillWithNoTablets);
        RunResult expected = RunResult.builder()
                .withStatus(SUCCESS)
                .withRunMinutes(properProgramConfigurationWithRinseProgramHalfLevelFillWithNoTablets.getProgram().getTimeInMinutes())
                .build();

        assertThat(expected, samePropertyValuesAs(result));
    }

    @Test
    public void properProgramWithDoorsOpenedWithNoTabletsWithRinseProgram_fail_doorOpen() {
        when(door.closed()).thenReturn(false);

        RunResult result = dishWasher.start(properProgramConfigurationWithRinseProgramHalfLevelFillWithNoTablets);
        RunResult expected = RunResult.builder()
                .withStatus(Status.DOOR_OPEN)
                .build();

        assertThat(expected, samePropertyValuesAs(result));
    }

    @Test
    public void properProgramWithDoorsClosedWithTabletsWithRinseProgram_fail_errorFilter() {
        when(door.closed()).thenReturn(true);
        when(dirtFilter.capacity()).thenReturn(49.0d);

        RunResult result = dishWasher.start(properProgramConfigurationWithRinseProgramHalfLevelFillWithTablets);
        RunResult expected = RunResult.builder()
                .withStatus(Status.ERROR_FILTER)
                .build();

        assertThat(expected, samePropertyValuesAs(result));
    }
}
