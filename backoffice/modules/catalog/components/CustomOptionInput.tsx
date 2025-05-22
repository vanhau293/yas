import { ProductVariation } from '@catalogModels/ProductVariation';
import React, { useState, useEffect } from 'react';
import { Form, Badge } from 'react-bootstrap';
import { toast } from 'react-toastify';

type CustomInputProps = {
  defaultValue?: string;
  onChange?: (value: string[]) => void;
  productVariations?: ProductVariation[];
};

const CustomOptionInput: React.FC<CustomInputProps> = ({
  defaultValue,
  onChange,
  productVariations,
}) => {
  const [inputValue, setInputValue] = useState<string>('');
  const [selectedOptions, setSelectedOptions] = useState<string[]>([]);

  const handleInputChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    setInputValue(event.target.value);
  };

  const handleKeyDown = (event: React.KeyboardEvent<HTMLInputElement>) => {
    if (event.key === ';') {
      event.preventDefault();
      if (inputValue.trim()) {
        const newOptions = [...selectedOptions, inputValue.trim()];
        setSelectedOptions(newOptions);
        setInputValue('');

        if (onChange) {
          onChange(newOptions);
        }
      }
    }
  };

  const handleRemoveOption = (option: string) => {
    const optionExists = productVariations?.some((variation) =>
      Object.values(variation.optionValuesByOptionId).includes(option)
    );

    if (optionExists) {
      return toast.warning(`The option: ${option} is in use`);
    }

    const newOptions = selectedOptions.filter((item) => item !== option);
    setSelectedOptions(newOptions);

    if (onChange) {
      onChange(newOptions);
    }
  };

  useEffect(() => {
    if (defaultValue) {
      const parsedOptions = defaultValue.split(',').map((option) => option.trim());
      setSelectedOptions(parsedOptions);
    }
  }, [defaultValue]);

  return (
    <div
      style={{
        width: '100%',
        border: '1px solid #ced4da',
        borderRadius: '4px',
        padding: '5px',
        display: 'flex',
        flexWrap: 'wrap',
        alignItems: 'center',
      }}
    >
      {selectedOptions.map((option) => (
        <Badge
          key={option}
          pill
          bg="success"
          style={{
            display: 'inline-flex',
            alignItems: 'center',
            marginRight: '5px',
            padding: '10px',
            cursor: 'default',
          }}
        >
          <span style={{ marginRight: '8px' }}>{option}</span>
          <Badge
            pill
            bg="light"
            style={{ cursor: 'pointer', marginLeft: '5px', padding: '4px 4px', color: 'black' }}
            onClick={() => handleRemoveOption(option)}
          >
            &times;
          </Badge>
        </Badge>
      ))}
      <Form.Control
        type="text"
        value={inputValue}
        onChange={handleInputChange}
        onKeyDown={handleKeyDown}
        placeholder="Type and press ';' for select option value"
        style={{ border: 'none', boxShadow: 'none', flex: 1, minWidth: '150px' }}
      />
    </div>
  );
};

export default CustomOptionInput;
