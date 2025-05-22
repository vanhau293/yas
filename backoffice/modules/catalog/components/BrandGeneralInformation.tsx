import { FieldErrorsImpl, UseFormRegister, UseFormSetValue, UseFormTrigger } from 'react-hook-form';
import slugify from 'slugify';

import { Input, Switch } from '../../../common/items/Input';
import { SLUG_FIELD_PATTERN } from '../constants/validationPattern';
import { Brand } from '../models/Brand';

type Props = {
  register: UseFormRegister<Brand>;
  errors: FieldErrorsImpl<Brand>;
  setValue: UseFormSetValue<Brand>;
  trigger: UseFormTrigger<Brand>;
  brand?: Brand;
};

const BrandGeneralInformation = ({ register, errors, setValue, trigger, brand }: Props) => {
  const onNameChange = async (event: React.ChangeEvent<HTMLInputElement>) => {
    setValue('slug', slugify(event.target.value, { lower: true, strict: true }));
    await trigger('slug');
    await trigger('name');
    await trigger('isPublish');
  };
  return (
    <>
      <Input
        labelText="Name"
        field="name"
        defaultValue={brand?.name}
        register={register}
        registerOptions={{
          required: { value: true, message: 'Brand name is required' },
          onChange: onNameChange,
        }}
        error={errors.name?.message}
      />
      <Input
        labelText="Slug"
        field="slug"
        defaultValue={brand?.slug}
        register={register}
        registerOptions={{
          required: { value: true, message: 'Slug brand is required' },
          pattern: {
            value: SLUG_FIELD_PATTERN,
            message:
              'Slug must not contain special characters except dash and all characters must be lowercase',
          },
        }}
        error={errors.slug?.message}
      />
      <Switch
        labelText="Publish"
        field="isPublish"
        defaultChecked={brand?.isPublish}
        register={register}
      />
    </>
  );
};

export default BrandGeneralInformation;
