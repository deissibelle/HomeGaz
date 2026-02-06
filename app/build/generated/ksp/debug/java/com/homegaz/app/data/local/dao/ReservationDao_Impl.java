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
import com.homegaz.app.data.local.entity.ReservationEntity;
import java.lang.Class;
import java.lang.Double;
import java.lang.Exception;
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

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class ReservationDao_Impl implements ReservationDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<ReservationEntity> __insertionAdapterOfReservationEntity;

  private final EntityDeletionOrUpdateAdapter<ReservationEntity> __deletionAdapterOfReservationEntity;

  private final EntityDeletionOrUpdateAdapter<ReservationEntity> __updateAdapterOfReservationEntity;

  private final SharedSQLiteStatement __preparedStmtOfDeleteOldDrafts;

  public ReservationDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfReservationEntity = new EntityInsertionAdapter<ReservationEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `reservations` (`id`,`userId`,`gasPointId`,`brand`,`weight`,`quantity`,`deliveryMode`,`needsConsigne`,`consigneAmount`,`subtotal`,`deliveryFee`,`total`,`deliveryAddress`,`deliveryLatitude`,`deliveryLongitude`,`notes`,`createdAt`,`status`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final ReservationEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getUserId());
        statement.bindString(3, entity.getGasPointId());
        statement.bindString(4, entity.getBrand());
        statement.bindLong(5, entity.getWeight());
        statement.bindLong(6, entity.getQuantity());
        statement.bindString(7, entity.getDeliveryMode());
        final int _tmp = entity.getNeedsConsigne() ? 1 : 0;
        statement.bindLong(8, _tmp);
        statement.bindLong(9, entity.getConsigneAmount());
        statement.bindLong(10, entity.getSubtotal());
        statement.bindLong(11, entity.getDeliveryFee());
        statement.bindLong(12, entity.getTotal());
        if (entity.getDeliveryAddress() == null) {
          statement.bindNull(13);
        } else {
          statement.bindString(13, entity.getDeliveryAddress());
        }
        if (entity.getDeliveryLatitude() == null) {
          statement.bindNull(14);
        } else {
          statement.bindDouble(14, entity.getDeliveryLatitude());
        }
        if (entity.getDeliveryLongitude() == null) {
          statement.bindNull(15);
        } else {
          statement.bindDouble(15, entity.getDeliveryLongitude());
        }
        if (entity.getNotes() == null) {
          statement.bindNull(16);
        } else {
          statement.bindString(16, entity.getNotes());
        }
        statement.bindLong(17, entity.getCreatedAt());
        statement.bindString(18, entity.getStatus());
      }
    };
    this.__deletionAdapterOfReservationEntity = new EntityDeletionOrUpdateAdapter<ReservationEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `reservations` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final ReservationEntity entity) {
        statement.bindString(1, entity.getId());
      }
    };
    this.__updateAdapterOfReservationEntity = new EntityDeletionOrUpdateAdapter<ReservationEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `reservations` SET `id` = ?,`userId` = ?,`gasPointId` = ?,`brand` = ?,`weight` = ?,`quantity` = ?,`deliveryMode` = ?,`needsConsigne` = ?,`consigneAmount` = ?,`subtotal` = ?,`deliveryFee` = ?,`total` = ?,`deliveryAddress` = ?,`deliveryLatitude` = ?,`deliveryLongitude` = ?,`notes` = ?,`createdAt` = ?,`status` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final ReservationEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getUserId());
        statement.bindString(3, entity.getGasPointId());
        statement.bindString(4, entity.getBrand());
        statement.bindLong(5, entity.getWeight());
        statement.bindLong(6, entity.getQuantity());
        statement.bindString(7, entity.getDeliveryMode());
        final int _tmp = entity.getNeedsConsigne() ? 1 : 0;
        statement.bindLong(8, _tmp);
        statement.bindLong(9, entity.getConsigneAmount());
        statement.bindLong(10, entity.getSubtotal());
        statement.bindLong(11, entity.getDeliveryFee());
        statement.bindLong(12, entity.getTotal());
        if (entity.getDeliveryAddress() == null) {
          statement.bindNull(13);
        } else {
          statement.bindString(13, entity.getDeliveryAddress());
        }
        if (entity.getDeliveryLatitude() == null) {
          statement.bindNull(14);
        } else {
          statement.bindDouble(14, entity.getDeliveryLatitude());
        }
        if (entity.getDeliveryLongitude() == null) {
          statement.bindNull(15);
        } else {
          statement.bindDouble(15, entity.getDeliveryLongitude());
        }
        if (entity.getNotes() == null) {
          statement.bindNull(16);
        } else {
          statement.bindString(16, entity.getNotes());
        }
        statement.bindLong(17, entity.getCreatedAt());
        statement.bindString(18, entity.getStatus());
        statement.bindString(19, entity.getId());
      }
    };
    this.__preparedStmtOfDeleteOldDrafts = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM reservations WHERE status = 'DRAFT' AND createdAt < ?";
        return _query;
      }
    };
  }

  @Override
  public Object insertReservation(final ReservationEntity reservation,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfReservationEntity.insert(reservation);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteReservation(final ReservationEntity reservation,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfReservationEntity.handle(reservation);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateReservation(final ReservationEntity reservation,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfReservationEntity.handle(reservation);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteOldDrafts(final long timestamp,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteOldDrafts.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, timestamp);
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
          __preparedStmtOfDeleteOldDrafts.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object getUserReservations(final String userId,
      final Continuation<? super List<ReservationEntity>> $completion) {
    final String _sql = "SELECT * FROM reservations WHERE userId = ? ORDER BY createdAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, userId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<ReservationEntity>>() {
      @Override
      @NonNull
      public List<ReservationEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "userId");
          final int _cursorIndexOfGasPointId = CursorUtil.getColumnIndexOrThrow(_cursor, "gasPointId");
          final int _cursorIndexOfBrand = CursorUtil.getColumnIndexOrThrow(_cursor, "brand");
          final int _cursorIndexOfWeight = CursorUtil.getColumnIndexOrThrow(_cursor, "weight");
          final int _cursorIndexOfQuantity = CursorUtil.getColumnIndexOrThrow(_cursor, "quantity");
          final int _cursorIndexOfDeliveryMode = CursorUtil.getColumnIndexOrThrow(_cursor, "deliveryMode");
          final int _cursorIndexOfNeedsConsigne = CursorUtil.getColumnIndexOrThrow(_cursor, "needsConsigne");
          final int _cursorIndexOfConsigneAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "consigneAmount");
          final int _cursorIndexOfSubtotal = CursorUtil.getColumnIndexOrThrow(_cursor, "subtotal");
          final int _cursorIndexOfDeliveryFee = CursorUtil.getColumnIndexOrThrow(_cursor, "deliveryFee");
          final int _cursorIndexOfTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "total");
          final int _cursorIndexOfDeliveryAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "deliveryAddress");
          final int _cursorIndexOfDeliveryLatitude = CursorUtil.getColumnIndexOrThrow(_cursor, "deliveryLatitude");
          final int _cursorIndexOfDeliveryLongitude = CursorUtil.getColumnIndexOrThrow(_cursor, "deliveryLongitude");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final List<ReservationEntity> _result = new ArrayList<ReservationEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ReservationEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpUserId;
            _tmpUserId = _cursor.getString(_cursorIndexOfUserId);
            final String _tmpGasPointId;
            _tmpGasPointId = _cursor.getString(_cursorIndexOfGasPointId);
            final String _tmpBrand;
            _tmpBrand = _cursor.getString(_cursorIndexOfBrand);
            final int _tmpWeight;
            _tmpWeight = _cursor.getInt(_cursorIndexOfWeight);
            final int _tmpQuantity;
            _tmpQuantity = _cursor.getInt(_cursorIndexOfQuantity);
            final String _tmpDeliveryMode;
            _tmpDeliveryMode = _cursor.getString(_cursorIndexOfDeliveryMode);
            final boolean _tmpNeedsConsigne;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfNeedsConsigne);
            _tmpNeedsConsigne = _tmp != 0;
            final int _tmpConsigneAmount;
            _tmpConsigneAmount = _cursor.getInt(_cursorIndexOfConsigneAmount);
            final int _tmpSubtotal;
            _tmpSubtotal = _cursor.getInt(_cursorIndexOfSubtotal);
            final int _tmpDeliveryFee;
            _tmpDeliveryFee = _cursor.getInt(_cursorIndexOfDeliveryFee);
            final int _tmpTotal;
            _tmpTotal = _cursor.getInt(_cursorIndexOfTotal);
            final String _tmpDeliveryAddress;
            if (_cursor.isNull(_cursorIndexOfDeliveryAddress)) {
              _tmpDeliveryAddress = null;
            } else {
              _tmpDeliveryAddress = _cursor.getString(_cursorIndexOfDeliveryAddress);
            }
            final Double _tmpDeliveryLatitude;
            if (_cursor.isNull(_cursorIndexOfDeliveryLatitude)) {
              _tmpDeliveryLatitude = null;
            } else {
              _tmpDeliveryLatitude = _cursor.getDouble(_cursorIndexOfDeliveryLatitude);
            }
            final Double _tmpDeliveryLongitude;
            if (_cursor.isNull(_cursorIndexOfDeliveryLongitude)) {
              _tmpDeliveryLongitude = null;
            } else {
              _tmpDeliveryLongitude = _cursor.getDouble(_cursorIndexOfDeliveryLongitude);
            }
            final String _tmpNotes;
            if (_cursor.isNull(_cursorIndexOfNotes)) {
              _tmpNotes = null;
            } else {
              _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            _item = new ReservationEntity(_tmpId,_tmpUserId,_tmpGasPointId,_tmpBrand,_tmpWeight,_tmpQuantity,_tmpDeliveryMode,_tmpNeedsConsigne,_tmpConsigneAmount,_tmpSubtotal,_tmpDeliveryFee,_tmpTotal,_tmpDeliveryAddress,_tmpDeliveryLatitude,_tmpDeliveryLongitude,_tmpNotes,_tmpCreatedAt,_tmpStatus);
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
  public Object getReservationById(final String id,
      final Continuation<? super ReservationEntity> $completion) {
    final String _sql = "SELECT * FROM reservations WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<ReservationEntity>() {
      @Override
      @Nullable
      public ReservationEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "userId");
          final int _cursorIndexOfGasPointId = CursorUtil.getColumnIndexOrThrow(_cursor, "gasPointId");
          final int _cursorIndexOfBrand = CursorUtil.getColumnIndexOrThrow(_cursor, "brand");
          final int _cursorIndexOfWeight = CursorUtil.getColumnIndexOrThrow(_cursor, "weight");
          final int _cursorIndexOfQuantity = CursorUtil.getColumnIndexOrThrow(_cursor, "quantity");
          final int _cursorIndexOfDeliveryMode = CursorUtil.getColumnIndexOrThrow(_cursor, "deliveryMode");
          final int _cursorIndexOfNeedsConsigne = CursorUtil.getColumnIndexOrThrow(_cursor, "needsConsigne");
          final int _cursorIndexOfConsigneAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "consigneAmount");
          final int _cursorIndexOfSubtotal = CursorUtil.getColumnIndexOrThrow(_cursor, "subtotal");
          final int _cursorIndexOfDeliveryFee = CursorUtil.getColumnIndexOrThrow(_cursor, "deliveryFee");
          final int _cursorIndexOfTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "total");
          final int _cursorIndexOfDeliveryAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "deliveryAddress");
          final int _cursorIndexOfDeliveryLatitude = CursorUtil.getColumnIndexOrThrow(_cursor, "deliveryLatitude");
          final int _cursorIndexOfDeliveryLongitude = CursorUtil.getColumnIndexOrThrow(_cursor, "deliveryLongitude");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final ReservationEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpUserId;
            _tmpUserId = _cursor.getString(_cursorIndexOfUserId);
            final String _tmpGasPointId;
            _tmpGasPointId = _cursor.getString(_cursorIndexOfGasPointId);
            final String _tmpBrand;
            _tmpBrand = _cursor.getString(_cursorIndexOfBrand);
            final int _tmpWeight;
            _tmpWeight = _cursor.getInt(_cursorIndexOfWeight);
            final int _tmpQuantity;
            _tmpQuantity = _cursor.getInt(_cursorIndexOfQuantity);
            final String _tmpDeliveryMode;
            _tmpDeliveryMode = _cursor.getString(_cursorIndexOfDeliveryMode);
            final boolean _tmpNeedsConsigne;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfNeedsConsigne);
            _tmpNeedsConsigne = _tmp != 0;
            final int _tmpConsigneAmount;
            _tmpConsigneAmount = _cursor.getInt(_cursorIndexOfConsigneAmount);
            final int _tmpSubtotal;
            _tmpSubtotal = _cursor.getInt(_cursorIndexOfSubtotal);
            final int _tmpDeliveryFee;
            _tmpDeliveryFee = _cursor.getInt(_cursorIndexOfDeliveryFee);
            final int _tmpTotal;
            _tmpTotal = _cursor.getInt(_cursorIndexOfTotal);
            final String _tmpDeliveryAddress;
            if (_cursor.isNull(_cursorIndexOfDeliveryAddress)) {
              _tmpDeliveryAddress = null;
            } else {
              _tmpDeliveryAddress = _cursor.getString(_cursorIndexOfDeliveryAddress);
            }
            final Double _tmpDeliveryLatitude;
            if (_cursor.isNull(_cursorIndexOfDeliveryLatitude)) {
              _tmpDeliveryLatitude = null;
            } else {
              _tmpDeliveryLatitude = _cursor.getDouble(_cursorIndexOfDeliveryLatitude);
            }
            final Double _tmpDeliveryLongitude;
            if (_cursor.isNull(_cursorIndexOfDeliveryLongitude)) {
              _tmpDeliveryLongitude = null;
            } else {
              _tmpDeliveryLongitude = _cursor.getDouble(_cursorIndexOfDeliveryLongitude);
            }
            final String _tmpNotes;
            if (_cursor.isNull(_cursorIndexOfNotes)) {
              _tmpNotes = null;
            } else {
              _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            _result = new ReservationEntity(_tmpId,_tmpUserId,_tmpGasPointId,_tmpBrand,_tmpWeight,_tmpQuantity,_tmpDeliveryMode,_tmpNeedsConsigne,_tmpConsigneAmount,_tmpSubtotal,_tmpDeliveryFee,_tmpTotal,_tmpDeliveryAddress,_tmpDeliveryLatitude,_tmpDeliveryLongitude,_tmpNotes,_tmpCreatedAt,_tmpStatus);
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

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
