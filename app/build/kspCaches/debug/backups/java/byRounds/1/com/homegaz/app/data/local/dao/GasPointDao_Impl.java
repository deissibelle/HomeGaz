package com.homegaz.app.data.local.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.homegaz.app.data.local.entity.GasPointEntity;
import java.lang.Class;
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
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class GasPointDao_Impl implements GasPointDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<GasPointEntity> __insertionAdapterOfGasPointEntity;

  private final SharedSQLiteStatement __preparedStmtOfDeleteAllGasPoints;

  private final SharedSQLiteStatement __preparedStmtOfDeleteOldGasPoints;

  public GasPointDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfGasPointEntity = new EntityInsertionAdapter<GasPointEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `gas_points` (`id`,`name`,`brand`,`latitude`,`longitude`,`address`,`phone`,`openingHours`,`isOpen`,`availableWeights`,`rating`,`reviewsCount`,`pricePerKg`,`cachedAt`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final GasPointEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getName());
        statement.bindString(3, entity.getBrand());
        statement.bindDouble(4, entity.getLatitude());
        statement.bindDouble(5, entity.getLongitude());
        statement.bindString(6, entity.getAddress());
        statement.bindString(7, entity.getPhone());
        statement.bindString(8, entity.getOpeningHours());
        final int _tmp = entity.isOpen() ? 1 : 0;
        statement.bindLong(9, _tmp);
        statement.bindString(10, entity.getAvailableWeights());
        statement.bindDouble(11, entity.getRating());
        statement.bindLong(12, entity.getReviewsCount());
        statement.bindDouble(13, entity.getPricePerKg());
        statement.bindLong(14, entity.getCachedAt());
      }
    };
    this.__preparedStmtOfDeleteAllGasPoints = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM gas_points";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteOldGasPoints = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM gas_points WHERE cachedAt < ?";
        return _query;
      }
    };
  }

  @Override
  public Object insertGasPoints(final List<GasPointEntity> points,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfGasPointEntity.insert(points);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertGasPoint(final GasPointEntity point,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfGasPointEntity.insert(point);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteAllGasPoints(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteAllGasPoints.acquire();
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
          __preparedStmtOfDeleteAllGasPoints.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteOldGasPoints(final long timestamp,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteOldGasPoints.acquire();
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
          __preparedStmtOfDeleteOldGasPoints.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object getAllGasPoints(final Continuation<? super List<GasPointEntity>> $completion) {
    final String _sql = "SELECT * FROM gas_points";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<GasPointEntity>>() {
      @Override
      @NonNull
      public List<GasPointEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfBrand = CursorUtil.getColumnIndexOrThrow(_cursor, "brand");
          final int _cursorIndexOfLatitude = CursorUtil.getColumnIndexOrThrow(_cursor, "latitude");
          final int _cursorIndexOfLongitude = CursorUtil.getColumnIndexOrThrow(_cursor, "longitude");
          final int _cursorIndexOfAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "address");
          final int _cursorIndexOfPhone = CursorUtil.getColumnIndexOrThrow(_cursor, "phone");
          final int _cursorIndexOfOpeningHours = CursorUtil.getColumnIndexOrThrow(_cursor, "openingHours");
          final int _cursorIndexOfIsOpen = CursorUtil.getColumnIndexOrThrow(_cursor, "isOpen");
          final int _cursorIndexOfAvailableWeights = CursorUtil.getColumnIndexOrThrow(_cursor, "availableWeights");
          final int _cursorIndexOfRating = CursorUtil.getColumnIndexOrThrow(_cursor, "rating");
          final int _cursorIndexOfReviewsCount = CursorUtil.getColumnIndexOrThrow(_cursor, "reviewsCount");
          final int _cursorIndexOfPricePerKg = CursorUtil.getColumnIndexOrThrow(_cursor, "pricePerKg");
          final int _cursorIndexOfCachedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "cachedAt");
          final List<GasPointEntity> _result = new ArrayList<GasPointEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final GasPointEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpBrand;
            _tmpBrand = _cursor.getString(_cursorIndexOfBrand);
            final double _tmpLatitude;
            _tmpLatitude = _cursor.getDouble(_cursorIndexOfLatitude);
            final double _tmpLongitude;
            _tmpLongitude = _cursor.getDouble(_cursorIndexOfLongitude);
            final String _tmpAddress;
            _tmpAddress = _cursor.getString(_cursorIndexOfAddress);
            final String _tmpPhone;
            _tmpPhone = _cursor.getString(_cursorIndexOfPhone);
            final String _tmpOpeningHours;
            _tmpOpeningHours = _cursor.getString(_cursorIndexOfOpeningHours);
            final boolean _tmpIsOpen;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsOpen);
            _tmpIsOpen = _tmp != 0;
            final String _tmpAvailableWeights;
            _tmpAvailableWeights = _cursor.getString(_cursorIndexOfAvailableWeights);
            final float _tmpRating;
            _tmpRating = _cursor.getFloat(_cursorIndexOfRating);
            final int _tmpReviewsCount;
            _tmpReviewsCount = _cursor.getInt(_cursorIndexOfReviewsCount);
            final double _tmpPricePerKg;
            _tmpPricePerKg = _cursor.getDouble(_cursorIndexOfPricePerKg);
            final long _tmpCachedAt;
            _tmpCachedAt = _cursor.getLong(_cursorIndexOfCachedAt);
            _item = new GasPointEntity(_tmpId,_tmpName,_tmpBrand,_tmpLatitude,_tmpLongitude,_tmpAddress,_tmpPhone,_tmpOpeningHours,_tmpIsOpen,_tmpAvailableWeights,_tmpRating,_tmpReviewsCount,_tmpPricePerKg,_tmpCachedAt);
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
  public Flow<List<GasPointEntity>> observeAllGasPoints() {
    final String _sql = "SELECT * FROM gas_points";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"gas_points"}, new Callable<List<GasPointEntity>>() {
      @Override
      @NonNull
      public List<GasPointEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfBrand = CursorUtil.getColumnIndexOrThrow(_cursor, "brand");
          final int _cursorIndexOfLatitude = CursorUtil.getColumnIndexOrThrow(_cursor, "latitude");
          final int _cursorIndexOfLongitude = CursorUtil.getColumnIndexOrThrow(_cursor, "longitude");
          final int _cursorIndexOfAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "address");
          final int _cursorIndexOfPhone = CursorUtil.getColumnIndexOrThrow(_cursor, "phone");
          final int _cursorIndexOfOpeningHours = CursorUtil.getColumnIndexOrThrow(_cursor, "openingHours");
          final int _cursorIndexOfIsOpen = CursorUtil.getColumnIndexOrThrow(_cursor, "isOpen");
          final int _cursorIndexOfAvailableWeights = CursorUtil.getColumnIndexOrThrow(_cursor, "availableWeights");
          final int _cursorIndexOfRating = CursorUtil.getColumnIndexOrThrow(_cursor, "rating");
          final int _cursorIndexOfReviewsCount = CursorUtil.getColumnIndexOrThrow(_cursor, "reviewsCount");
          final int _cursorIndexOfPricePerKg = CursorUtil.getColumnIndexOrThrow(_cursor, "pricePerKg");
          final int _cursorIndexOfCachedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "cachedAt");
          final List<GasPointEntity> _result = new ArrayList<GasPointEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final GasPointEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpBrand;
            _tmpBrand = _cursor.getString(_cursorIndexOfBrand);
            final double _tmpLatitude;
            _tmpLatitude = _cursor.getDouble(_cursorIndexOfLatitude);
            final double _tmpLongitude;
            _tmpLongitude = _cursor.getDouble(_cursorIndexOfLongitude);
            final String _tmpAddress;
            _tmpAddress = _cursor.getString(_cursorIndexOfAddress);
            final String _tmpPhone;
            _tmpPhone = _cursor.getString(_cursorIndexOfPhone);
            final String _tmpOpeningHours;
            _tmpOpeningHours = _cursor.getString(_cursorIndexOfOpeningHours);
            final boolean _tmpIsOpen;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsOpen);
            _tmpIsOpen = _tmp != 0;
            final String _tmpAvailableWeights;
            _tmpAvailableWeights = _cursor.getString(_cursorIndexOfAvailableWeights);
            final float _tmpRating;
            _tmpRating = _cursor.getFloat(_cursorIndexOfRating);
            final int _tmpReviewsCount;
            _tmpReviewsCount = _cursor.getInt(_cursorIndexOfReviewsCount);
            final double _tmpPricePerKg;
            _tmpPricePerKg = _cursor.getDouble(_cursorIndexOfPricePerKg);
            final long _tmpCachedAt;
            _tmpCachedAt = _cursor.getLong(_cursorIndexOfCachedAt);
            _item = new GasPointEntity(_tmpId,_tmpName,_tmpBrand,_tmpLatitude,_tmpLongitude,_tmpAddress,_tmpPhone,_tmpOpeningHours,_tmpIsOpen,_tmpAvailableWeights,_tmpRating,_tmpReviewsCount,_tmpPricePerKg,_tmpCachedAt);
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
  public Object getGasPointById(final String id,
      final Continuation<? super GasPointEntity> $completion) {
    final String _sql = "SELECT * FROM gas_points WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<GasPointEntity>() {
      @Override
      @Nullable
      public GasPointEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfBrand = CursorUtil.getColumnIndexOrThrow(_cursor, "brand");
          final int _cursorIndexOfLatitude = CursorUtil.getColumnIndexOrThrow(_cursor, "latitude");
          final int _cursorIndexOfLongitude = CursorUtil.getColumnIndexOrThrow(_cursor, "longitude");
          final int _cursorIndexOfAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "address");
          final int _cursorIndexOfPhone = CursorUtil.getColumnIndexOrThrow(_cursor, "phone");
          final int _cursorIndexOfOpeningHours = CursorUtil.getColumnIndexOrThrow(_cursor, "openingHours");
          final int _cursorIndexOfIsOpen = CursorUtil.getColumnIndexOrThrow(_cursor, "isOpen");
          final int _cursorIndexOfAvailableWeights = CursorUtil.getColumnIndexOrThrow(_cursor, "availableWeights");
          final int _cursorIndexOfRating = CursorUtil.getColumnIndexOrThrow(_cursor, "rating");
          final int _cursorIndexOfReviewsCount = CursorUtil.getColumnIndexOrThrow(_cursor, "reviewsCount");
          final int _cursorIndexOfPricePerKg = CursorUtil.getColumnIndexOrThrow(_cursor, "pricePerKg");
          final int _cursorIndexOfCachedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "cachedAt");
          final GasPointEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpBrand;
            _tmpBrand = _cursor.getString(_cursorIndexOfBrand);
            final double _tmpLatitude;
            _tmpLatitude = _cursor.getDouble(_cursorIndexOfLatitude);
            final double _tmpLongitude;
            _tmpLongitude = _cursor.getDouble(_cursorIndexOfLongitude);
            final String _tmpAddress;
            _tmpAddress = _cursor.getString(_cursorIndexOfAddress);
            final String _tmpPhone;
            _tmpPhone = _cursor.getString(_cursorIndexOfPhone);
            final String _tmpOpeningHours;
            _tmpOpeningHours = _cursor.getString(_cursorIndexOfOpeningHours);
            final boolean _tmpIsOpen;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsOpen);
            _tmpIsOpen = _tmp != 0;
            final String _tmpAvailableWeights;
            _tmpAvailableWeights = _cursor.getString(_cursorIndexOfAvailableWeights);
            final float _tmpRating;
            _tmpRating = _cursor.getFloat(_cursorIndexOfRating);
            final int _tmpReviewsCount;
            _tmpReviewsCount = _cursor.getInt(_cursorIndexOfReviewsCount);
            final double _tmpPricePerKg;
            _tmpPricePerKg = _cursor.getDouble(_cursorIndexOfPricePerKg);
            final long _tmpCachedAt;
            _tmpCachedAt = _cursor.getLong(_cursorIndexOfCachedAt);
            _result = new GasPointEntity(_tmpId,_tmpName,_tmpBrand,_tmpLatitude,_tmpLongitude,_tmpAddress,_tmpPhone,_tmpOpeningHours,_tmpIsOpen,_tmpAvailableWeights,_tmpRating,_tmpReviewsCount,_tmpPricePerKg,_tmpCachedAt);
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
  public Object getGasPointsByBrand(final String brand,
      final Continuation<? super List<GasPointEntity>> $completion) {
    final String _sql = "SELECT * FROM gas_points WHERE brand = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, brand);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<GasPointEntity>>() {
      @Override
      @NonNull
      public List<GasPointEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfBrand = CursorUtil.getColumnIndexOrThrow(_cursor, "brand");
          final int _cursorIndexOfLatitude = CursorUtil.getColumnIndexOrThrow(_cursor, "latitude");
          final int _cursorIndexOfLongitude = CursorUtil.getColumnIndexOrThrow(_cursor, "longitude");
          final int _cursorIndexOfAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "address");
          final int _cursorIndexOfPhone = CursorUtil.getColumnIndexOrThrow(_cursor, "phone");
          final int _cursorIndexOfOpeningHours = CursorUtil.getColumnIndexOrThrow(_cursor, "openingHours");
          final int _cursorIndexOfIsOpen = CursorUtil.getColumnIndexOrThrow(_cursor, "isOpen");
          final int _cursorIndexOfAvailableWeights = CursorUtil.getColumnIndexOrThrow(_cursor, "availableWeights");
          final int _cursorIndexOfRating = CursorUtil.getColumnIndexOrThrow(_cursor, "rating");
          final int _cursorIndexOfReviewsCount = CursorUtil.getColumnIndexOrThrow(_cursor, "reviewsCount");
          final int _cursorIndexOfPricePerKg = CursorUtil.getColumnIndexOrThrow(_cursor, "pricePerKg");
          final int _cursorIndexOfCachedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "cachedAt");
          final List<GasPointEntity> _result = new ArrayList<GasPointEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final GasPointEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpBrand;
            _tmpBrand = _cursor.getString(_cursorIndexOfBrand);
            final double _tmpLatitude;
            _tmpLatitude = _cursor.getDouble(_cursorIndexOfLatitude);
            final double _tmpLongitude;
            _tmpLongitude = _cursor.getDouble(_cursorIndexOfLongitude);
            final String _tmpAddress;
            _tmpAddress = _cursor.getString(_cursorIndexOfAddress);
            final String _tmpPhone;
            _tmpPhone = _cursor.getString(_cursorIndexOfPhone);
            final String _tmpOpeningHours;
            _tmpOpeningHours = _cursor.getString(_cursorIndexOfOpeningHours);
            final boolean _tmpIsOpen;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsOpen);
            _tmpIsOpen = _tmp != 0;
            final String _tmpAvailableWeights;
            _tmpAvailableWeights = _cursor.getString(_cursorIndexOfAvailableWeights);
            final float _tmpRating;
            _tmpRating = _cursor.getFloat(_cursorIndexOfRating);
            final int _tmpReviewsCount;
            _tmpReviewsCount = _cursor.getInt(_cursorIndexOfReviewsCount);
            final double _tmpPricePerKg;
            _tmpPricePerKg = _cursor.getDouble(_cursorIndexOfPricePerKg);
            final long _tmpCachedAt;
            _tmpCachedAt = _cursor.getLong(_cursorIndexOfCachedAt);
            _item = new GasPointEntity(_tmpId,_tmpName,_tmpBrand,_tmpLatitude,_tmpLongitude,_tmpAddress,_tmpPhone,_tmpOpeningHours,_tmpIsOpen,_tmpAvailableWeights,_tmpRating,_tmpReviewsCount,_tmpPricePerKg,_tmpCachedAt);
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

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
