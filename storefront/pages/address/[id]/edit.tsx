import { NextPage } from 'next';
import Head from 'next/head';
import AddressForm from '../../../modules/address/components/AddressForm';
import { getAddress, updateAddress } from '../../../modules/address/services/AddressService';
import { Address } from '../../../modules/address/models/AddressModel';
import { useForm } from 'react-hook-form';
import { useEffect, useState } from 'react';
import { useRouter } from 'next/router';
import { toast } from 'react-toastify';
import { UPDATE_SUCCESSFULLY } from '../../../common/constants/Common';
import ProfileLayout from '@/common/components/ProfileLayout';
import { BreadcrumbModel } from '@/modules/breadcrumb/model/BreadcrumbModel';

const crumb: BreadcrumbModel[] = [
  {
    pageName: 'Home',
    url: '/',
  },
  {
    pageName: 'Address',
    url: '/address',
  },
  {
    pageName: 'Edit',
    url: '#',
  },
];

const EditAddress: NextPage = () => {
  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<Address>();

  const router = useRouter();
  const { id } = router.query;
  router.isReady = true;
  const [address, setAddress] = useState<Address>();

  useEffect(() => {
    if (id) {
      getAddress(id as string).then((res) => {
        if (res.title && res.title == 'Not Found') return;
        setAddress(res);
      });
    }
  }, [id]);

  const onSubmit = async (data: any) => {
    const {
      contactName,
      phone,
      addressLine1,
      city,
      zipCode,
      districtId,
      stateOrProvinceId,
      countryId,
    } = data;
    const request: Address = {
      contactName,
      phone,
      addressLine1,
      city,
      zipCode,
      districtId: parseInt(districtId),
      stateOrProvinceId: parseInt(stateOrProvinceId),
      countryId: parseInt(countryId),
    };
    updateAddress(id as string, request)
      .then(() => {
        toast.success(UPDATE_SUCCESSFULLY);
        router.push('/address');
      })
      .catch((e) => console.log(e));
  };

  if (!id) return <></>;
  if (id && !address) {
    return (
      <>
        <Head>
          <title>Edit Address</title>
        </Head>
        <div className="container pt-5" style={{ minHeight: '550px' }}>
          <h2 className="mb-3">Edit Address</h2>
          <p>Address not found</p>
        </div>
      </>
    );
  }
  return (
    <ProfileLayout breadcrumb={crumb} title="Edit Address" menuActive="address">
      <AddressForm
        handleSubmit={handleSubmit(onSubmit)}
        register={register}
        errors={errors}
        address={address}
        isDisplay={true}
        setValue={() => {}}
      />
    </ProfileLayout>
  );
};

export default EditAddress;
