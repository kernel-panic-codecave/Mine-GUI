/*
 * Mine GUI
 * Copyright (C) 2022 WitherTech
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.withertech.mine_gui.widget.data;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * An observable mutable property. Observable properties are containers for values
 * that can be modified and listened to.
 *
 * <p>The naming convention for {@code ObservableProperty} getters follows the convention
 * {@code <property name>Property}. For example, the {@code WWidget.hovered} property can be retrieved with
 * {@link io.github.cottonmc.cotton.gui.widget.WWidget#hoveredProperty() hoveredProperty()}.
 *
 * @param <T> the contained value type
 * @experimental
 * @since 4.2.0
 */
@ApiStatus.Experimental
// TODO: Add filters
public final class ObservableProperty<T> implements ObservableView<T>
{
	private static final String DEFAULT_NAME = "<unnamed>";
	private final List<ChangeListener<? super T>> listeners = new ArrayList<>();
	private final boolean allowNull;
	private final String name;
	private boolean hasValue;
	private T value;

	private ObservableProperty(@Nullable T value, boolean hasValue, boolean allowNull, String name)
	{
		this.value = value;
		this.hasValue = hasValue;
		this.allowNull = allowNull;
		this.name = name;

		if (hasValue && value == null && !allowNull)
		{
			throw new NullPointerException("Cannot initialise nonnull property " + name + " with null value");
		}
	}

	/**
	 * Creates a "late init" property without an initial value.
	 * The created property will throw an exception if it has not been initialised yet.
	 *
	 * @param <T> the contained value type
	 * @return the created empty property builder
	 */
	public static <T> Builder<T> empty()
	{
		return new Builder<>(null, false);
	}

	/**
	 * Creates a property with an initial value.
	 *
	 * @param initialValue the initial value
	 * @param <T>          the contained value type
	 * @return the created property
	 */
	public static <T> Builder<T> of(T initialValue)
	{
		return new Builder<>(initialValue, true);
	}

	@Override
	public boolean hasValue()
	{
		return hasValue;
	}

	@Override
	public T get()
	{
		if (!hasValue)
		{
			throw new IllegalStateException("Property " + name + " not initialised!");
		}

		return value;
	}

	/**
	 * Sets this property to a constant value.
	 *
	 * @param value the new value
	 * @throws NullPointerException if the value is null and nulls aren't allowed
	 */
	public void set(T value)
	{
		if (value == null && !allowNull)
			throw new NullPointerException("Trying to set null value for nonnull property " + name);
		T oldValue = this.value;
		this.value = value;
		hasValue = true;

		if (oldValue != value)
		{
			for (ChangeListener<? super T> listener : listeners)
			{
				listener.onPropertyChange(this, oldValue, value);
			}
		}
	}

	/**
	 * Returns a read-only view of this property.
	 * The result is not an instance of {@link ObservableProperty},
	 * and thus can't be mutated.
	 *
	 * @return an observable view of this property
	 */
	public ObservableView<T> readOnly()
	{
		// Missing delegates from Kotlin... :(
		return new ObservableView<>()
		{
			@Override
			public boolean hasValue()
			{
				return ObservableProperty.this.hasValue();
			}

			@Override
			public T get()
			{
				return ObservableProperty.this.get();
			}

			@Override
			public void addListener(ChangeListener<? super T> listener)
			{
				ObservableProperty.this.addListener(listener);
			}

			@Override
			public void removeListener(ChangeListener<? super T> listener)
			{
				ObservableProperty.this.removeListener(listener);
			}
		};
	}

	/**
	 * {@return the name of this property}
	 */
	public String getName()
	{
		return name;
	}

	@Override
	public void addListener(ChangeListener<? super T> listener)
	{
		Objects.requireNonNull(listener);
		listeners.add(listener);
	}

	@Override
	public void removeListener(ChangeListener<? super T> listener)
	{
		Objects.requireNonNull(listener);
		listeners.remove(listener);
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o) return true;
		if (!(o instanceof ObservableProperty<?> that)) return false;
		return allowNull == that.allowNull && hasValue == that.hasValue && Objects.equals(listeners, that.listeners) && Objects.equals(getName(), that.getName()) && Objects.equals(value, that.value);
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(listeners, allowNull, getName(), hasValue, value);
	}

	@Override
	public String toString()
	{
		return "ObservableProperty{" +
				"listeners=" + listeners +
				", allowNull=" + allowNull +
				", name='" + name + '\'' +
				", hasValue=" + hasValue +
				", value=" + value +
				'}';
	}

	/**
	 * A builder for properties.
	 *
	 * @param <T> the contained value type
	 */
	public static final class Builder<T>
	{
		private final T initialValue;
		private final boolean hasValue;
		private String name = DEFAULT_NAME;
		private boolean allowNull = true;

		Builder(@Nullable T initialValue, boolean hasValue)
		{
			this.initialValue = initialValue;
			this.hasValue = hasValue;
		}

		/**
		 * Disallows null values.
		 *
		 * @return this builder
		 */
		public Builder<T> nonnull()
		{
			allowNull = false;
			return this;
		}

		/**
		 * Sets the name of this property, which is used in debug messages.
		 */
		public Builder<T> name(String name)
		{
			this.name = Objects.requireNonNull(name, "name");
			return this;
		}

		/**
		 * Builds the observable property.
		 *
		 * @return the created property
		 */
		public ObservableProperty<T> build()
		{
			return new ObservableProperty<>(initialValue, hasValue, allowNull, name);
		}

		@Override
		public boolean equals(Object o)
		{
			if (this == o) return true;
			if (!(o instanceof Builder<?> builder)) return false;
			return hasValue == builder.hasValue && allowNull == builder.allowNull && Objects.equals(initialValue, builder.initialValue) && Objects.equals(name, builder.name);
		}

		@Override
		public int hashCode()
		{
			return Objects.hash(initialValue, hasValue, name, allowNull);
		}

		@Override
		public String toString()
		{
			return "Builder{" +
					"initialValue=" + initialValue +
					", hasValue=" + hasValue +
					", name='" + name + '\'' +
					", allowNull=" + allowNull +
					'}';
		}
	}
}
