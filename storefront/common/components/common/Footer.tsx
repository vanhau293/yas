import Link from 'next/link';
import Image from 'next/image';

import iconPay01 from '../../../asset/icons/icon-pay-01.jpg';
import iconPay02 from '../../../asset/icons/icon-pay-02.jpg';
import iconPay03 from '../../../asset/icons/icon-pay-03.jpg';
import iconPay04 from '../../../asset/icons/icon-pay-04.jpg';
import iconPay05 from '../../../asset/icons/icon-pay-05.jpg';
import { addSampleData } from '@/modules/sampledata/services/SampleDataService';
import { AddSampleModel } from '@/modules/sampledata/models/AddSampleModel';
import { toastError, toastSuccess } from '../../../modules/catalog/services/ToastService';

const Footer = () => {
  const handleFetch = async () => {
    try {
      const addSampleModel: AddSampleModel = { message: 'addSampleModel' };
      const response = await addSampleData(addSampleModel);
      if (response.message !== null) {
        toastSuccess('Add sample data successfully !');
        window.location.reload();
      }
    } catch (err: any) {
      if (err.message == 403) toastError('Please login to add sample data!');
      else toastError('Add sample data failed. Try again');
    }
  };
  return (
    <footer className="bg3 p-t-75 p-b-32">
      <div className="container">
        <div className="row">
          <button
            type="submit"
            className="cl0 bg1 bor-radius hov-btn p-lr-15 trans-04"
            style={{
              width: 150,
              height: 40,
              borderRadius: 5,
              fontWeight: 600,
              fontSize: 16,
              marginBottom: '15px',
            }}
            onClick={handleFetch}
          >
            Sample Data
          </button>
        </div>
        <div className="row">
          <div className="col-sm-6 col-lg-3 p-b-50">
            <h4 className="cl0 p-b-30">Categories</h4>
            <Link href="" className="cl7 hov-cl trans-04">
              Laptop
            </Link>
          </div>

          <div className="col-sm-6 col-lg-3 p-b-50">
            <h4 className="cl0 p-b-30">Help</h4>
            <Link href="" className="cl7 hov-cl trans-04">
              About
            </Link>
          </div>

          <div className="col-sm-6 col-lg-3 p-b-50">
            <h4 className="cl0 p-b-30">GET IN TOUCH</h4>

            <p className="cl7 size-201">
              Etown 4, 364 Cong Hoa Street, Tan Binh District, Ho Chi Minh City
            </p>

            <div className="p-t-27">
              <Link
                href="https://github.com/nashtech-garage/yas/"
                target={'_blank'}
                className="fs-18 cl7 hov-cl trans-04 m-r-16"
              >
                <i className="bi bi-facebook"></i>
              </Link>

              <Link
                href="https://github.com/nashtech-garage/yas/"
                target={'_blank'}
                className="fs-18 cl7 hov-cl trans-04 m-r-16"
              >
                <i className="bi bi-instagram"></i>
              </Link>

              <Link
                href="https://github.com/nashtech-garage/yas/"
                target={'_blank'}
                className="fs-18 cl7 hov-cl trans-04 m-r-16"
              >
                <i className="bi bi-github"></i>
              </Link>
            </div>
          </div>

          <div className="col-sm-6 col-lg-3 p-b-50">
            <h4 className="cl0 p-b-30">Newsletter</h4>

            <form>
              <div className="wrap-input w-full p-b-4">
                <input
                  className="input cl7"
                  type="text"
                  name="email"
                  placeholder="yas-sample@yas.com"
                />
                <div className="focus-input trans-04"></div>
              </div>

              <div className="p-t-18">
                <button className="cl0 bg1 bor-radius hov-btn p-lr-15 trans-04">
                  <Link
                    href="https://github.com/nashtech-garage/yas/"
                    target={'_blank'}
                    className="text-dark"
                  >
                    Subscribe
                  </Link>
                </button>
              </div>
            </form>
          </div>
        </div>

        <div className="p-t-40">
          <div className="flex-c-m flex-w p-b-18 fs-2 gap-2">
            <Image src={iconPay01} alt="ICON_PAY" width={50} height={30} />
            <Image src={iconPay02} alt="ICON_PAY" width={50} height={30} />
            <Image src={iconPay03} alt="ICON_PAY" width={50} height={30} />
            <Image src={iconPay04} alt="ICON_PAY" width={50} height={30} />
            <Image src={iconPay05} alt="ICON_PAY" width={50} height={30} />
          </div>

          <p className="stext-107 cl6 txt-center">
            Copyright &copy; {`${new Date().getFullYear()} `}
            All rights reserved | Distributed by
            <Link href="" target={'_blank'} rel="noopener noreferrer" className="hov-cl text-white">
              {' '}
              Yas Team
            </Link>{' '}
            with ❤️
          </p>
        </div>
      </div>
    </footer>
  );
};

export default Footer;
