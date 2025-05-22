import Link from 'next/link';
import { useRouter } from 'next/router';
import { useEffect, useRef, useState } from 'react';
import { data_menu_top_no_login } from '@/asset/data/data_header_client';
import { SEARCH_URL } from '@/common/constants/Common';
import { useCartContext } from '@/context/CartContext';
import { SearchSuggestion } from '@/modules/search/models/SearchSuggestion';
import { getSuggestions } from '@/modules/search/services/SearchService';
import { getCategoriesSuggestions } from '@/modules/catalog/services/CategoryService';
import { useDebounce } from '@/utils/useDebounce';

type Props = {
  children: React.ReactNode;
};

const SUGGESTION_MIN = 3;
const SUGGESTION_MAX = 10;

const Header = ({ children }: Props) => {
  const router = useRouter();
  const { keyword: keywordParams } = router.query;
  const formRef = useRef<HTMLFormElement>(null);
  const inputRef = useRef<HTMLInputElement>(null);
  const [showDropdown, setShowDropdown] = useState(false);
  const [isExpand, setIsExpand] = useState(false);
  const { numberCartItems } = useCartContext();

  const [searchSuggestions, setSearchSuggestions] = useState<SearchSuggestion[]>([]);
  const [categorySuggestions, setCategorySuggestions] = useState<string[]>([]);
  const [searchInput, setSearchInput] = useState<string>('');

  const keyword = useDebounce(searchInput, 300);

  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      if (formRef.current && !formRef.current.contains(event.target as Node)) {
        setShowDropdown(false);
      }
    };

    document.addEventListener('mousedown', handleClickOutside);

    return () => {
      document.removeEventListener('mousedown', handleClickOutside);
    };
  }, []);

  useEffect(() => {
    if (keywordParams) {
      setSearchInput(keywordParams as string);
    }
  }, [keywordParams]);

  useEffect(() => {
    if (keyword) {
      fetchSearchSuggestions(keyword);
    } else {
      setSearchSuggestions([]);
    }
  }, [keyword]);

  useEffect(() => {
    const fetchTopCategories = async () => {
      try {
        const categories = await getCategoriesSuggestions();
        setCategorySuggestions(categories);
      } catch (error) {
        console.error('Failed to fetch category suggestions', error);
      }
    };

    fetchTopCategories();
  }, []);

  const fetchSearchSuggestions = (keyword: string) => {
    getSuggestions(keyword)
      .then((suggestions) => {
        setSearchSuggestions(suggestions.productNames);
      })
      .catch((err) => {
        console.error(`Failed to get search suggestions. ${err}`);
      });
  };

  const handleInputFocus = () => {
    setShowDropdown(true);
  };

  const handleSubmitSearch = (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    if (searchInput.trim()) {
      router.push(`${SEARCH_URL}/?keyword=${searchInput}`);
    }
    setShowDropdown(false);
    inputRef.current?.blur();
  };

  return (
    <header>
      <div className="container-header">
        <nav className="top-bar">
          <div className="top-bar-container container">
            <div className="left-top-bar">Free shipping for standard order over $100</div>

            <div className="right-top-bar d-flex h-full">
              {data_menu_top_no_login.map((item) => (
                <Link href={item.links} className="d-flex align-items-center px-4" key={item.id}>
                  {item.icon && (
                    <div className="icon-header-bell-question">
                      <i className={item.icon}></i>
                    </div>
                  )}
                  {item.name}
                </Link>
              ))}
              <div className="d-flex align-items-center px-4">{children}</div>
            </div>
          </div>
        </nav>

        <nav className="limiter-menu-desktop container">
          {/* <!-- Logo desktop --> */}
          <Link href="/" className="header-logo me-3">
            <h3 className="text-black">Yas - Storefront</h3>
          </Link>

          {/* <!-- Search --> */}
          <div className="header-search flex-grow-1">
            <div className="search-wrapper">
              <form onSubmit={handleSubmitSearch} className="search-form" ref={formRef}>
                <label htmlFor="header-search" className="search-icon">
                  <i className="bi bi-search"></i>
                </label>
                <input
                  id="header-search"
                  ref={inputRef}
                  className="search-input"
                  placeholder="What you will find today?"
                  onFocus={handleInputFocus}
                  value={searchInput}
                  onChange={(e) => setSearchInput(e.target.value)}
                />

                {showDropdown && (
                  <div className="search-auto-complete">
                    <div className="suggestion">
                      {searchSuggestions
                        .slice(0, isExpand ? SUGGESTION_MAX : SUGGESTION_MIN)
                        .map((item) => (
                          <Link
                            href={`${SEARCH_URL}/?keyword=${item.name}`}
                            className="search-suggestion-item"
                            key={item.name}
                            onClick={() => {
                              setSearchInput(item.name);
                              setShowDropdown(false);
                            }}
                          >
                            <div className="icon">
                              <i className="bi bi-search"></i>
                            </div>
                            <div className="keyword">{item.name}</div>
                          </Link>
                        ))}
                      {searchSuggestions.length > SUGGESTION_MIN && (
                        <div className="search-suggestion-action">
                          <span onClick={() => setIsExpand((prev) => !prev)}>
                            {isExpand ? (
                              <>
                                Collapsed <i className="bi bi-chevron-up"></i>
                              </>
                            ) : (
                              <>
                                Expand <i className="bi bi-chevron-down"></i>
                              </>
                            )}
                          </span>
                        </div>
                      )}
                    </div>
                    <div className="bottom-widgets"></div>
                  </div>
                )}

                <button type="submit" className="search-button">
                  Search
                </button>
              </form>
            </div>

            <div className="search-suggestion">
              {categorySuggestions.map((item) => (
                <Link href={`${SEARCH_URL}/?keyword=${item.toLocaleLowerCase()}`} key={item}>
                  <span>{item}</span>
                </Link>
              ))}
            </div>
          </div>

          {/* <!-- Cart --> */}
          <Link className="header-cart" href="/cart">
            <div className="icon-cart">
              <i className="bi bi-cart3"></i>
            </div>
            <div className="quantity-cart">{numberCartItems}</div>
          </Link>
        </nav>

        <nav className="limiter-menu-desktop container"></nav>
      </div>

      {showDropdown && <div className="container-layer"></div>}
      <div className="lower-container"></div>
    </header>
  );
};

export default Header;
