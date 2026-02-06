package com.homegaz.app.data.local.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.homegaz.app.data.local.entity.OrderEntity;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class OrderDao_Impl implements OrderDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<OrderEntity> __insertionAdapterOfOrderEntity;

  private final EntityDeletionOrUpdateAdapter<OrderEntity> __updateAdapterOfOrderEntity;

  private final SharedSQLiteStatement __preparedStmtOfDeleteAllOrders;

  public OrderDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfOrderEntity = new EntityInsertionAdapter<OrderEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `orders` (`id`,`userId`,`reservationId`,`paymentMethod`,`status`,`createdAt`,`updatedAt`,`estimatedDeliveryTime`,`actualDeliveryTime`,`cancelReason`) VALUES (?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final OrderEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getUserId());
        statement.bindString(3, entity.getReservationId());
        statement.bindString(4, entity.getPaymentMethod());
        statement.bindString(5, entity.getStatus());
        statement.bindLong(6, entity.getCreatedAt());
        statement.bindLong(7, entity.getUpdatedAt());
        if (entity.getEstimatedDeliveryTime() == null) {
          statement.bindNull(8);
        } else {
          statement.bindLong(8, entity.getEstimatedDeliveryTime());
        }
        if (entity.getActualDeliveryTime() == null) {
          statement.bindNull(9);
        } else {
          statement.bindLong(9, entity.getActualDeliveryTime());
        }
        if (entity.getCancelReason() == null) {
          statement.bindNull(10);
        } else {
          statement.bindString(10, entity.getCancelReason());
        }
      }
    };
    this.__updateAdapterOfOrderEntity = new EntityDeletionOrUpdateAdapter<OrderEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `orders` SET `id` = ?,`userId` = ?,`reservationId` = ?,`paymentMethod` = ?,`status` = ?,`createdAt` = ?,`updatedAt` = ?,`estimatedDeliveryTime` = ?,`actualDeliveryTime` = ?,`cancelReason` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final OrderEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getUserId());
        statement.bindString(3, entity.getReservationId());
        statement.bindString(4, entity.getPaymentMethod());
        statement.bindString(5, entity.getStatus());
        statement.bindLong(6, entity.getCreatedAt());
        statement.bindLong(7, entity.getUpdatedAt());
        if (entity.getEstimatedDeliveryTime() == null) {
          statement.bindNull(8);
        } else {
          statement.bindLong(8, entity.getEstimatedDeliveryTime());
        }
        if (entity.getActualDeliveryTime() == null) {
          statement.bindNull(9);
        } else {
          statement.bindLong(9, entity.getActualDeliveryTime());
        }
        if (entity.getCancelReason() == null) {
          statement.bindNull(10);
        } else {
          statement.bindString(10, entity.getCancelReason());
        }
        statement.bindString(11, entity.getId());
      }
    };
    this.__preparedStmtOfDeleteAllOrders = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM orders";
        return _query;
      }
    };
  }

  @Override
  public Object insertOrder(final OrderEntity order, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfOrderEntity.insert(order);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateOrder(final OrderEntity order, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfOrderEntity.handle(order);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteAllOrders(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteAllOrders.acquire();
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDeleteAllOrders.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object getUserOrders(final String userId,
      final Continuation<? super List<OrderEntity>> $completion) {
    final String _sql = "SELECT * FROM orders WHERE userId = ? ORDER BY createdAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, userId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<OrderEntity>>() {
      @Override
      @NonNull
      public List<OrderEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "userId");
          final int _cursorIndexOfReservationId = CursorUtil.getColumnIndexOrThrow(_cursor, "reservationId");
          final int _cursorIndexOfPaymentMethod = CursorUtil.getColumnIndexOrThrow(_cursor, "paymentMethod");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final int _cursorIndexOfEstimatedDeliveryTime = CursorUtil.getColumnIndexOrThrow(_cursor, "estimatedDeliveryTime");
          final int _cursorIndexOfActualDeliveryTime = CursorUtil.getColumnIndexOrThrow(_cursor, "actualDeliveryTime");
          final int _cursorIndexOfCancelReason = CursorUtil.getColumnIndexOrThrow(_cursor, "cancelReason");
          final List<OrderEntity> _result = new ArrayList<OrderEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final OrderEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpUserId;
            _tmpUserId = _cursor.getString(_cursorIndexOfUserId);
            final String _tmpReservationId;
            _tmpReservationId = _cursor.getString(_cursorIndexOfReservationId);
            final String _tmpPaymentMethod;
            _tmpPaymentMethod = _cursor.getString(_cursorIndexOfPaymentMethod);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            final Long _tmpEstimatedDeliveryTime;
            if (_cursor.isNull(_cursorIndexOfEstimatedDeliveryTime)) {
              _tmpEstimatedDeliveryTime = null;
            } else {
              _tmpEstimatedDeliveryTime = _cursor.getLong(_cursorIndexOfEstimatedDeliveryTime);
            }
            final Long _tmpActualDeliveryTime;
            if (_cursor.isNull(_cursorIndexOfActualDeliveryTime)) {
              _tmpActualDeliveryTime = null;
            } else {
              _tmpActualDeliveryTime = _cursor.getLong(_cursorIndexOfActualDeliveryTime);
            }
            final String _tmpCancelReason;
            if (_cursor.isNull(_cursorIndexOfCancelReason)) {
              _tmpCancelReason = null;
            } else {
              _tmpCancelReason = _cursor.getString(_cursorIndexOfCancelReason);
            }
            _item = new OrderEntity(_tmpId,_tmpUserId,_tmpReservationId,_tmpPaymentMethod,_tmpStatus,_tmpCreatedAt,_tmpUpdatedAt,_tmpEstimatedDeliveryTime,_tmpActualDeliveryTime,_tmpCancelReason);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<OrderEntity>> observeUserOrders(final String userId) {
    final String _sql = "SELECT * FROM orders WHERE userId = ? ORDER BY createdAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, userId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"orders"}, new Callable<List<OrderEntity>>() {
      @Override
      @NonNull
      public List<OrderEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "userId");
          final int _cursorIndexOfReservationId = CursorUtil.getColumnIndexOrThrow(_cursor, "reservationId");
          final int _cursorIndexOfPaymentMethod = CursorUtil.getColumnIndexOrThrow(_cursor, "paymentMethod");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final int _cursorIndexOfEstimatedDeliveryTime = CursorUtil.getColumnIndexOrThrow(_cursor, "estimatedDeliveryTime");
          final int _cursorIndexOfActualDeliveryTime = CursorUtil.getColumnIndexOrThrow(_cursor, "actualDeliveryTime");
          final int _cursorIndexOfCancelReason = CursorUtil.getColumnIndexOrThrow(_cursor, "cancelReason");
          final List<OrderEntity> _result = new ArrayList<OrderEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final OrderEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpUserId;
            _tmpUserId = _cursor.getString(_cursorIndexOfUserId);
            final String _tmpReservationId;
            _tmpReservationId = _cursor.getString(_cursorIndexOfReservationId);
            final String _tmpPaymentMethod;
            _tmpPaymentMethod = _cursor.getString(_cursorIndexOfPaymentMethod);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            final Long _tmpEstimatedDeliveryTime;
            if (_cursor.isNull(_cursorIndexOfEstimatedDeliveryTime)) {
              _tmpEstimatedDeliveryTime = null;
            } else {
              _tmpEstimatedDeliveryTime = _cursor.getLong(_cursorIndexOfEstimatedDeliveryTime);
            }
            final Long _tmpActualDeliveryTime;
            if (_cursor.isNull(_cursorIndexOfActualDeliveryTime)) {
              _tmpActualDeliveryTime = null;
            } else {
              _tmpActualDeliveryTime = _cursor.getLong(_cursorIndexOfActualDeliveryTime);
            }
            final String _tmpCancelReason;
            if (_cursor.isNull(_cursorIndexOfCancelReason)) {
              _tmpCancelReason = null;
            } else {
              _tmpCancelReason = _cursor.getString(_cursorIndexOfCancelReason);
            }
            _item = new OrderEntity(_tmpId,_tmpUserId,_tmpReservationId,_tmpPaymentMethod,_tmpStatus,_tmpCreatedAt,_tmpUpdatedAt,_tmpEstimatedDeliveryTime,_tmpActualDeliveryTime,_tmpCancelReason);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getOrderById(final String id, final Continuation<? super OrderEntity> $completion) {
    final String _sql = "SELECT * FROM orders WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<OrderEntity>() {
      @Override
      @Nullable
      public OrderEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "userId");
          final int _cursorIndexOfReservationId = CursorUtil.getColumnIndexOrThrow(_cursor, "reservationId");
          final int _cursorIndexOfPaymentMethod = CursorUtil.getColumnIndexOrThrow(_cursor, "paymentMethod");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final int _cursorIndexOfEstimatedDeliveryTime = CursorUtil.getColumnIndexOrThrow(_cursor, "estimatedDeliveryTime");
          final int _cursorIndexOfActualDeliveryTime = CursorUtil.getColumnIndexOrThrow(_cursor, "actualDeliveryTime");
          final int _cursorIndexOfCancelReason = CursorUtil.getColumnIndexOrThrow(_cursor, "cancelReason");
          final OrderEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpUserId;
            _tmpUserId = _cursor.getString(_cursorIndexOfUserId);
            final String _tmpReservationId;
            _tmpReservationId = _cursor.getString(_cursorIndexOfReservationId);
            final String _tmpPaymentMethod;
            _tmpPaymentMethod = _cursor.getString(_cursorIndexOfPaymentMethod);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            final Long _tmpEstimatedDeliveryTime;
            if (_cursor.isNull(_cursorIndexOfEstimatedDeliveryTime)) {
              _tmpEstimatedDeliveryTime = null;
            } else {
              _tmpEstimatedDeliveryTime = _cursor.getLong(_cursorIndexOfEstimatedDeliveryTime);
            }
            final Long _tmpActualDeliveryTime;
            if (_cursor.isNull(_cursorIndexOfActualDeliveryTime)) {
              _tmpActualDeliveryTime = null;
            } else {
              _tmpActualDeliveryTime = _cursor.getLong(_cursorIndexOfActualDeliveryTime);
            }
            final String _tmpCancelReason;
            if (_cursor.isNull(_cursorIndexOfCancelReason)) {
              _tmpCancelReason = null;
            } else {
              _tmpCancelReason = _cursor.getString(_cursorIndexOfCancelReason);
            }
            _result = new OrderEntity(_tmpId,_tmpUserId,_tmpReservationId,_tmpPaymentMethod,_tmpStatus,_tmpCreatedAt,_tmpUpdatedAt,_tmpEstimatedDeliveryTime,_tmpActualDeliveryTime,_tmpCancelReason);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<OrderEntity> observeOrder(final String id) {
    final String _sql = "SELECT * FROM orders WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, id);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"orders"}, new Callable<OrderEntity>() {
      @Override
      @Nullable
      public OrderEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "userId");
          final int _cursorIndexOfReservationId = CursorUtil.getColumnIndexOrThrow(_cursor, "reservationId");
          final int _cursorIndexOfPaymentMethod = CursorUtil.getColumnIndexOrThrow(_cursor, "paymentMethod");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final int _cursorIndexOfEstimatedDeliveryTime = CursorUtil.getColumnIndexOrThrow(_cursor, "estimatedDeliveryTime");
          final int _cursorIndexOfActualDeliveryTime = CursorUtil.getColumnIndexOrThrow(_cursor, "actualDeliveryTime");
          final int _cursorIndexOfCancelReason = CursorUtil.getColumnIndexOrThrow(_cursor, "cancelReason");
          final OrderEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpUserId;
            _tmpUserId = _cursor.getString(_cursorIndexOfUserId);
            final String _tmpReservationId;
            _tmpReservationId = _cursor.getString(_cursorIndexOfReservationId);
            final String _tmpPaymentMethod;
            _tmpPaymentMethod = _cursor.getString(_cursorIndexOfPaymentMethod);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            final Long _tmpEstimatedDeliveryTime;
            if (_cursor.isNull(_cursorIndexOfEstimatedDeliveryTime)) {
              _tmpEstimatedDeliveryTime = null;
            } else {
              _tmpEstimatedDeliveryTime = _cursor.getLong(_cursorIndexOfEstimatedDeliveryTime);
            }
            final Long _tmpActualDeliveryTime;
            if (_cursor.isNull(_cursorIndexOfActualDeliveryTime)) {
              _tmpActualDeliveryTime = null;
            } else {
              _tmpActualDeliveryTime = _cursor.getLong(_cursorIndexOfActualDeliveryTime);
            }
            final String _tmpCancelReason;
            if (_cursor.isNull(_cursorIndexOfCancelReason)) {
              _tmpCancelReason = null;
            } else {
              _tmpCancelReason = _cursor.getString(_cursorIndexOfCancelReason);
            }
            _result = new OrderEntity(_tmpId,_tmpUserId,_tmpReservationId,_tmpPaymentMethod,_tmpStatus,_tmpCreatedAt,_tmpUpdatedAt,_tmpEstimatedDeliveryTime,_tmpActualDeliveryTime,_tmpCancelReason);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
