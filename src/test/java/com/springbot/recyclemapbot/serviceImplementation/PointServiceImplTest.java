/*
package com.springbot.recyclemapbot.serviceImplementation;

import com.springbot.recyclemapbot.DTO.PointDTO;
import com.springbot.recyclemapbot.model.Points;
import com.springbot.recyclemapbot.repository.PointRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

@ExtendWith(MockitoExtension.class)
public class PointServiceImplTest {

    @Mock
    private PointRepository pointRepository;

    @InjectMocks
    private PointServiceImpl pointService;

    @Test
    public void getPointDTOByPointId(){
        List<Points> points = createPoints();
        Long id = 2L;

        Mockito.when(pointRepository.getPointInfo(id)).thenReturn((PointDTO) points);

        PointDTO result = pointService.getPointInfo(id);

        Assertions.assertNotNull(result);
        Assertions.assertEquals((PointDTO) points.get(1), result);
    }

    private List<Points> createPoints() {
        PointDTO firstPoint;
        Points secondPoint = new Points();

        firstPoint.setId(1L);
        firstPoint.setTitle("Первая точка сбора мусора");
        firstPoint.setAddress("Екатеринбур, улица Ленина, дом 1");

        secondPoint.setId(2L);
        secondPoint.setTitle("Вторая точка сбора мусора");
        secondPoint.setAddress("Екатеринбур, улица Ленина, дом 2");


        return List.of(firstPoint, secondPoint);
    }
}
*/
