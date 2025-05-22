import type { NextPage } from 'next';
import Link from 'next/link';
import { useState, useEffect } from 'react';
import { Button, Stack, Table, Form } from 'react-bootstrap';
import ReactPaginate from 'react-paginate';
import { deleteCustomer, getCustomers } from '../../modules/customer/services/CustomerService';
import moment from 'moment';
import { toast } from 'react-toastify';
import { Customer } from '../../modules/customer/models/Customer';
import ModalDeleteCustom from '@commonItems/ModalDeleteCustom';
import { handleDeletingResponse } from '@commonServices/ResponseStatusHandlingService';
import { DEFAULT_PAGE_NUMBER } from '@constants/Common';

const Customers: NextPage = () => {
  const [userIdWantToDelete, setUserIdWantToDelete] = useState<string>('');
  const [userNameWantToDelete, setUserNameWantToDelete] = useState<string>('');
  const [showModalDelete, setShowModalDelete] = useState<boolean>(false);
  const [isLoading, setIsLoading] = useState(false);
  const [customers, setCustomers] = useState<Customer[]>([]);
  const [pageNo, setPageNo] = useState<number>(0);
  const [totalPage, setTotalPage] = useState<number>(0);
  const [totalUser, setTotalUser] = useState<number>(0);

  const handleClose: any = () => setShowModalDelete(false);
  const handleDelete: any = () => {
    if (userIdWantToDelete == '') {
      return;
    }

    deleteCustomer(userIdWantToDelete)
      .then((response) => {
        setShowModalDelete(false);
        handleDeletingResponse(response, userNameWantToDelete);
        setPageNo(DEFAULT_PAGE_NUMBER);
        getListCustomer();
      })
      .catch((error) => {
        console.log(error);
      });
  };

  const getListCustomer = () => {
    getCustomers(pageNo)
      .then((data) => {
        setCustomers(data.customers);
        setTotalPage(data.totalPage);
        setTotalUser(data.totalUser);
        setIsLoading(false);
      })
      .catch((err) => {
        toast.error('Something was wrong! Try later!');
      });
  };

  useEffect(() => {
    setIsLoading(true);
    getListCustomer();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [pageNo]);

  const changePage = ({ selected }: any) => {
    setPageNo(selected);
  };

  if (isLoading) return <p>Loading...</p>;
  if (!customers) return <p>No Customer</p>;
  return (
    <>
      <div className="row mt-5">
        <div className="col-md-8">
          <h2>Customer. Total {totalUser}</h2>
        </div>

        <div className="col-md-4 text-right">
          <Link href="/customers/create">
            <Button>Create Customer</Button>
          </Link>
        </div>
      </div>
      <Table striped bordered hover>
        <thead>
          <tr>
            <th>ID</th>
            <th>Username</th>
            <th>Email</th>
            <th>FirstName</th>
            <th>LastName</th>
            <th>Created Timestamp</th>
            <th>Action</th>
          </tr>
        </thead>
        <tbody>
          {(customers || []).map((customer) => (
            <tr key={customer.id}>
              <td>
                <Link href={`/customer/${customer.id}/view`}>{customer.id}</Link>
              </td>
              <td>{customer.username}</td>
              <td>{customer.email}</td>
              <td>{customer.firstName}</td>
              <td>{customer.lastName}</td>
              <td>{moment(customer.createdTimestamp).format('MMMM Do YYYY, h:mm:ss a')}</td>
              <td>
                <Stack direction="horizontal" gap={3}>
                  <Link href={`/customers/${customer.id}/edit`}>
                    <button className="btn btn-outline-primary btn-sm" type="button">
                      Edit
                    </button>
                  </Link>
                  <button
                    className="btn btn-outline-primary btn-sm"
                    type="button"
                    onClick={() => {
                      setShowModalDelete(true);
                      setUserIdWantToDelete(customer.id);
                      setUserNameWantToDelete(customer.username);
                    }}
                  >
                    Del
                  </button>
                </Stack>
              </td>
            </tr>
          ))}
        </tbody>
      </Table>
      <ModalDeleteCustom
        showModalDelete={showModalDelete}
        handleClose={handleClose}
        nameWantToDelete={userNameWantToDelete}
        handleDelete={handleDelete}
        action="delete"
      />
      {totalPage > 1 && (
        <ReactPaginate
          forcePage={pageNo}
          previousLabel={'Previous'}
          nextLabel={'Next'}
          pageCount={totalPage}
          onPageChange={changePage}
          containerClassName={'pagination-container'}
          previousClassName={'previous-btn'}
          nextClassName={'next-btn'}
          disabledClassName={'pagination-disabled'}
          activeClassName={'pagination-active'}
        />
      )}
    </>
  );
};

export default Customers;
