import { useRouter } from 'next/router';
import { useEffect, useState } from 'react';
import { Col, Container, Image, Row } from 'react-bootstrap';
import { toast } from 'react-toastify';

import noResultImg from '@/asset/images/no-result.png';
import BreadcrumbComponent from '@/common/components/BreadcrumbComponent';
import ProductCard from '@/common/components/ProductCard';
import { BreadcrumbModel } from '@/modules/breadcrumb/model/BreadcrumbModel';
import { Aggregations } from '@/modules/search/models/Aggregations';
import { ProductSearchResult } from '@/modules/search/models/ProductSearchResult';
import { SearchParams } from '@/modules/search/models/SearchParams';
import { ESortType, SortType } from '@/modules/search/models/SortType';
import { searchProducts } from '@/modules/search/services/SearchService';

import SearchResultLayout from '@/modules/search/components/SearchResultLayout';
import styles from '@/styles/modules/search/SearchPage.module.css';

const handleSortType = (sortType: string | string[] | undefined) => {
  if (sortType) {
    if (sortType === SortType.DEFAULT) {
      return ESortType.default;
    }
    if (sortType === SortType.PRICE_ASC) {
      return ESortType.priceAsc;
    }
    if (sortType === SortType.PRICE_DESC) {
      return ESortType.priceDesc;
    }
  }
  return undefined;
};

const SearchPage = () => {
  const router = useRouter();

  const [searchParams, setSearchParams] = useState<SearchParams>({
    keyword: '',
  });
  const [products, setProducts] = useState<ProductSearchResult[]>([]);
  const [totalElements, setTotalElements] = useState<number>(0);
  const [pageNo, setPageNo] = useState<number>(0);
  const [totalPage, setTotalPage] = useState<number>(0);
  const [aggregations, setAggregations] = useState<Aggregations>({});
  const isFilter = !!(
    searchParams.brand ??
    searchParams.category ??
    searchParams.attribute ??
    searchParams.minPrice ??
    searchParams.maxPrice
  );

  useEffect(() => {
    if (!router.isReady) return;
    const queryParams = {
      keyword: router.query.keyword ? String(router.query.keyword) : '',
      brand: router.query.brand ? String(router.query.brand) : undefined,
      category: router.query.category ? String(router.query.category) : undefined,
      attribute: router.query.attribute ? String(router.query.attribute) : undefined,
      minPrice:
        router.query.minPrice && +router.query.minPrice > 0 ? +router.query.minPrice : undefined,
      maxPrice:
        router.query.maxPrice && +router.query.maxPrice > 0 ? +router.query.maxPrice : undefined,
      sortType: handleSortType(router.query.sortType),
      page: router.query.page ? +router.query.page : undefined,
    };
    setSearchParams(queryParams);
  }, [router.isReady, router.query]);

  useEffect(() => {
    if (searchParams.keyword) {
      fetchSearchResult(searchParams);
    }
  }, [searchParams]);

  const fetchSearchResult = (data: SearchParams) => {
    searchProducts({ ...data, keyword: data.keyword.trim().toLowerCase() })
      .then((res) => {
        setProducts(res.products);
        setPageNo(res.pageNo);
        setTotalPage(res.totalPages);
        setTotalElements(res.totalElements);
        setAggregations(res.aggregations);
      })
      .catch((_error) => {
        toast.error('Something went wrong, please try again later');
      });
  };

  const crumb: BreadcrumbModel[] = [
    {
      pageName: 'Home',
      url: '/',
    },
    {
      pageName: `Search result for "${searchParams.keyword}"`,
      url: '#',
    },
  ];

  const renderProducts = () => (
    <Row xs={4} xl={5} className={styles['search-result__list']}>
      {products.map((product) => (
        <Col key={product.id}>
          <ProductCard
            className={['products-page']}
            product={{
              id: product.id,
              name: product.name,
              price: product.price,
              thumbnailUrl: '',
              slug: product.slug,
            }}
            thumbnailId={product.thumbnailId}
          />
        </Col>
      ))}
    </Row>
  );

  return (
    <div className={styles['search-page']}>
      <Container className={styles['search-container']}>
        <BreadcrumbComponent props={crumb} />

        <div className={styles['search-wrapper']}>
          {!isFilter ? (
            <>
              {products.length > 0 && (
                <SearchResultLayout
                  aggregations={aggregations}
                  searchParams={searchParams}
                  setSearchParams={setSearchParams}
                  pageNo={pageNo}
                  setPageNo={setPageNo}
                  totalPage={totalPage}
                  totalElements={totalElements}
                  isFilter={isFilter}
                >
                  {renderProducts()}
                </SearchResultLayout>
              )}

              {products.length === 0 && (
                <NoResultMessage
                  message="No result is found"
                  subMessage="Try using more generic keywords"
                />
              )}
            </>
          ) : (
            <SearchResultLayout
              aggregations={aggregations}
              searchParams={searchParams}
              setSearchParams={setSearchParams}
              pageNo={pageNo}
              setPageNo={setPageNo}
              totalPage={totalPage}
              totalElements={totalElements}
              isFilter={isFilter}
            >
              {products.length > 0 ? (
                renderProducts()
              ) : (
                <NoResultMessage message="No result is found with this filter" />
              )}
            </SearchResultLayout>
          )}
        </div>
      </Container>
    </div>
  );
};

const NoResultMessage = ({ message, subMessage }: { message: string; subMessage?: string }) => (
  <div className="text-center flex-grow-1 my-5">
    <Image src={noResultImg.src} alt="No result" style={{ width: '134px', height: '134px' }} />
    <h5 className="text-black mb-2">{message}</h5>
    {subMessage && <h5 className="text-black mb-2">{subMessage}</h5>}
  </div>
);

export default SearchPage;
