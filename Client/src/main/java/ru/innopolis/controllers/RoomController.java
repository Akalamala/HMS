package ru.innopolis.controllers;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import ru.innopolis.constants.AuthorizationConstant;
import ru.innopolis.dao.DAOServiceFactory;
import ru.innopolis.dao.IRoomDAOService;
import ru.innopolis.dao.entity.Client;
import ru.innopolis.dao.entity.addition.OrderDescription;
import ru.innopolis.dao.entity.addition.ExtendedRoom;
import ru.innopolis.exceptions.UserException;
import ru.innopolis.models.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Создано: Денис
 * Дата:  01.11.2016
 * Описание: Контроле по работе с номерами
 */
@RestController
public class RoomController extends BaseRestController {
    private Logger logger = Logger.getLogger(AuthorizationController.class.getName());

    @Autowired
    public RoomController(MessageSource messageSource) {
        super(messageSource);
    }

    /**
     * Получить список свободных комнат в отеле в указанный период
     *
     * @param modelRequest Параметры выборки
     * @param errors       Список ошибок валидации
     * @return Список свободных комнат. В случае ошибки - информация об ошибке
     */
    @GetMapping("/rooms/getAvailableRooms")
    public ResponseEntity findFreeRooms(@Valid AvailableRoomRequestModel modelRequest, Errors errors) {
        ResponseEntity response = getValidationErrorResponse(errors);
        if (response == null) {
            long hotelId = modelRequest.getHotelId();
            long roomTypeId = modelRequest.getRoomTypeId();
            Calendar from = modelRequest.getFrom();
            Calendar to = modelRequest.getTo();
            try {
                IRoomDAOService service = DAOServiceFactory.getInstance().createService(IRoomDAOService.class);
                List<ExtendedRoom> freeRooms = service.getFreeRoomsInHotel(hotelId, roomTypeId, from, to);
                List<AvailableRoomResponseModel> responseModelList = new ArrayList<>(freeRooms.size());
                freeRooms.forEach(room -> {
                    AvailableRoomResponseModel model = new AvailableRoomResponseModel();
                    model.setId(room.getId());
                    model.setRoomNumber(room.getRoomNumber());
                    model.setCost(room.getCost());
                    responseModelList.add(model);
                });
                HttpStatus status = freeRooms.isEmpty() ? HttpStatus.NO_CONTENT : HttpStatus.OK;
                response = new ResponseEntity(responseModelList, status);
            } catch (Exception e) {
                logger.log(Level.SEVERE, e.getMessage(), e);
                response = getGeneralErrorResponse();
            }
        }
        return response;
    }

    /**
     * Зарезервировать номер
     *
     * @param modelRequest Запрос на резервацию
     * @param errors       Список ошибок валидации
     * @param session      Сессия
     * @return {@link HttpStatus#OK} если бронь прошла успешно, в противном случае ошибка
     */
    @PutMapping("/private/rooms/book")
    public ResponseEntity bookRoom(@Valid @RequestBody BookModelRequest modelRequest, Errors errors, HttpSession session) {
        ResponseEntity response = getValidationErrorResponse(errors);
        if (response == null) {
            long roomId = modelRequest.getRoomId();
            Calendar from = modelRequest.getFrom();
            Calendar to = modelRequest.getTo();
            try {
                IRoomDAOService service = DAOServiceFactory.getInstance().createService(IRoomDAOService.class);
                Client client = (Client) session.getAttribute(AuthorizationConstant.CLIENT_KEY);
                service.bookRoom(client, roomId, from, to);
                response = new ResponseEntity(HttpStatus.OK);
            } catch (UserException e) {
                response = handleUserException(e);
            } catch (Exception e) {
                logger.log(Level.SEVERE, e.getMessage(), e);
                response = getGeneralErrorResponse();
            }
        }
        return response;
    }

    /**
     * Отменить заказ
     *
     * @param requestModel Запрос на удаление
     * @param errors       Список ошибок валидации
     * @param session      Сессия
     * @return 200 код если всё хорошо. В противном случаю код ошибки и описание
     */
    @PostMapping("/private/orders/cancel")
    public ResponseEntity cancelBook(@Valid @RequestBody CancelBookRequestModel requestModel, Errors errors, HttpSession session) {
        ResponseEntity response = getValidationErrorResponse(errors);
        if (response == null) {
            long orderId = requestModel.getOrderId();
            try {
                IRoomDAOService service = DAOServiceFactory.getInstance().createService(IRoomDAOService.class);
                Client client = (Client) session.getAttribute(AuthorizationConstant.CLIENT_KEY);
                service.cancelBook(client, orderId);
                response = new ResponseEntity(HttpStatus.OK);
            } catch (UserException e) {
                response = handleUserException(e);
            } catch (Exception e) {
                logger.log(Level.SEVERE, e.getMessage(), e);
                response = getGeneralErrorResponse();
            }
        }
        return response;
    }

    /**
     * Получить список заказов клиента
     *
     * @param session Сессия
     * @return Список заказов клиента
     */
    @GetMapping("/private/orders")
    public ResponseEntity getAllOrders(HttpSession session) {
        ResponseEntity response;
        Client client = (Client) session.getAttribute(AuthorizationConstant.CLIENT_KEY);
        try {
            IRoomDAOService service = DAOServiceFactory.getInstance().createService(IRoomDAOService.class);
            List<OrderDescription> orders = service.getOrdersByClient(client);
            List<OrderedRoomResponseModel> responseModel = new ArrayList<>(orders.size());
            orders.forEach(o -> {
                OrderedRoomResponseModel model = new OrderedRoomResponseModel();
                BeanUtils.copyProperties(o, model);
                responseModel.add(model);
            });
            HttpStatus status = responseModel.isEmpty() ? HttpStatus.NO_CONTENT : HttpStatus.OK;
            response = new ResponseEntity(responseModel, status);
        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            response = getGeneralErrorResponse();
        }
        return response;
    }
}
